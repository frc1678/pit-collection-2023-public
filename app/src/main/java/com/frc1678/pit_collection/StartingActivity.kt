package com.frc1678.pit_collection

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import com.frc1678.pit_collection.TeamListActivity.Companion.teamsList
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.android.synthetic.main.starting_activity.*
import kotlinx.android.synthetic.main.team_list_activity.*
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.Dns
import okhttp3.OkHttpClient
import java.io.File
import java.io.InputStream
import java.net.Inet4Address
import java.net.InetAddress

class Ipv4OnlyDns : Dns {
    override fun lookup(hostname: String): List<InetAddress> {
        val defaultAddresses = Dns.SYSTEM.lookup(hostname)
        val sortedAddresses = defaultAddresses.sortedBy {
            val isIpv4 = it is Inet4Address
            return@sortedBy isIpv4.not()
        }
        return sortedAddresses
    }
}

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
                val client = HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json()
                    }
                    engine {
                        preconfigured = OkHttpClient.Builder().dns(Ipv4OnlyDns()).build()
                    }
                }
                // Fetch the data

                val anotherFile = File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/event_key.txt")
                //check if file exists. if it does set constants.event to whats in the file.
                if (anotherFile.exists()) {
                    val inputStream: InputStream =
                        File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/event_key.txt").inputStream()
                    val inputString = inputStream.bufferedReader().use { it.readText() }
                    Constants.EVENT_KEY = inputString

                } else
                    Constants.EVENT_KEY = Constants.DEFAULT_KEY



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
                download_failed.setText("Missing team_list file ${Constants.EVENT_KEY}")


                /**this is the place where you can change event key in the error popup
                Text watcher that if there's stuff in the reload button it will reload but if there isn't
                 just don't do anything.
                 **/
                et_event.setText(Constants.DEFAULT_KEY)
                et_event.addTextChangedListener(object : TextWatcher {
                    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

                    }

                    override fun afterTextChanged(p0: Editable?) {
                        if (p0.toString().equals("")) {

                        }
                        else {
                            val file =
                                File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/event_key.txt")
                            file.writeText(p0.toString())
                        }


                    }
                })
                //All the additions here just check if the contents equal the default
                refresh_button.setOnClickListener {
                    if (et_event.text.toString().equals(Constants.DEFAULT_KEY)) {
                        val file =
                            File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/event_key.txt")
                        file.writeText(Constants.DEFAULT_KEY)
                    }
                    startActivity(Intent(this@StartingActivity, StartingActivity::class.java))

                }
            }
        }
    }



}
