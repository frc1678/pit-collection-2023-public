package com.frc1678.pit_collection

import android.content.Intent
import android.os.Bundle
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

/**
 * Downloads the team list file from Grosbeak. If this fails, the error is shown. Otherwise, opens
 * [TeamListActivity].
 */
class StartingActivity : CollectionActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
