package com.frc1678.pit_collection

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.frc1678.pit_collection.TeamListActivity.Companion.teamsList
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.okhttp.*
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.android.synthetic.main.starting_activity.error_message
import kotlinx.coroutines.runBlocking
import okhttp3.Dns
import okhttp3.OkHttpClient
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
                val client = HttpClient(OkHttp) {
                    install(ContentNegotiation) {
                        json()
                    }
                    engine {
                        preconfigured = OkHttpClient.Builder().dns(Ipv4OnlyDns()).build()
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
