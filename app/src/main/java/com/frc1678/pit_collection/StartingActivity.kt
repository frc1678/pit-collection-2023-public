package com.frc1678.pit_collection

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import com.frc1678.pit_collection.TeamListActivity.Companion.teamsList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.android.synthetic.main.starting_activity.error_message
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import java.io.File

/**
 * Downloads the team list file from Grosbeak or uses a cached team list. If this fails, the error
 * is shown. Otherwise, opens [TeamListActivity].
 */
class StartingActivity : CollectionActivity() {

    private val teamListFile =
        File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/team-list.json")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // If there is a cached team list, use it
        if (teamListFile.exists()) {
            // Parse the team list, which is stored as a JSON array
            teamsList =
                Json.parseToJsonElement(teamListFile.readText()).jsonArray.map { it.jsonPrimitive.content }
            // Start the main team list activity
            startActivity(Intent(this@StartingActivity, TeamListActivity::class.java))
        }
        // Use a coroutine scope for running a web request
        runBlocking {
            try {
                // Create client for the web request
                val client = HttpClient(CIO) {
                    install(ContentNegotiation) {
                        json()
                    }
                }
                // Fetch the data
                teamsList =
                    client.get("https://grosbeak.citruscircuits.org/api/team-list/${Constants.EVENT_KEY}") {
                        header("Authorization", Constants.GROSBEAK_AUTH_KEY)
                    }.body()
                // Cache the team list file
                teamListFile.writeText(Json.encodeToString(teamsList))
                // If the request worked, start the main team list activity
                startActivity(Intent(this@StartingActivity, TeamListActivity::class.java))
            } catch (t: Throwable) {
                // Set layout to the error message layout
                setContentView(R.layout.starting_activity)
                // Show the error message
                error_message.text = t.toString()
            }
        }
    }
}
