// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.Manifest
import android.app.ActivityOptions
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.serialization.kotlinx.json.json
import kotlinx.android.synthetic.main.team_list_activity.lv_teams_list
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.FileReader
import java.io.FileWriter

// Read the csv file, populate a listView, and start CollectionObjectiveDataActivity.
class TeamListActivity : CollectionActivity() {

    companion object {
        var teamsList = emptyList<String>()
        var starredTeams: MutableSet<String> = mutableSetOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StarredTeams.read(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.team_list_activity)
        setToolbarText(actionBar, supportActionBar)

        var mode = Constants.ModeSelection.OBJECTIVE
        putIntoStorage("mode_collection_select_activity", mode)
        for (team in StarredTeams.contents.toList()) {
            starredTeams.add(team.asString)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        val mapItem: MenuItem = menu.findItem(R.id.export_flags_button)
        val button = mapItem.actionView
        button.setOnClickListener {
            val flagsExport: MutableMap<String, List<String>> = mutableMapOf()
            for (team in teamsList) {
                val flagsList: MutableList<String> = mutableListOf()
                if (File("/storage/emulated/0/Download/${team.toInt()}_obj_pit.json").exists()) {
                    val teamJson = objJsonFileRead(team.toInt())
                    if ((teamJson.drivetrain == 1) && (!flagsList.contains("Mechanum"))) {
                        flagsList.add("Mechanum")
                    }
                    if (flagsList.isNotEmpty()) {
                        flagsExport[team] = flagsList
                    }
                }
            }
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("flags_export", flagsExport.toString())
            clipboard.setPrimaryClip(clip)
            Toast.makeText(this, "Copied to Clipboard", Toast.LENGTH_LONG).show()
        }
        return super.onCreateOptionsMenu(menu)
    }

    object StarredTeams {

        var contents = JsonArray()
        var gson = Gson()

        private val file =
            File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/starred_teams.json")

        fun read(context: Context) {
            try {
                contents = JsonParser().parse(FileReader(file)) as JsonArray
            } catch (e: Exception) {
                Log.e("StarredTeams.read", "Failed to read starred teams file")
            }
        }

        fun write() {
            var writer = FileWriter(file, false)
            gson.toJson(contents as JsonElement, writer)

            writer.close()
        }

        fun fileExists(): Boolean = file.exists()
    }

    // Starts the mode selection activity of the previously selected selection mode
    private fun intentToMatchInput() {
        this.getSharedPreferences("PREFS", 0).edit().remove("mode_collection_select_activity")
            .apply()
        startActivity(
            Intent(this, ModeCollectionSelectActivity::class.java),
            ActivityOptions.makeSceneTransitionAnimation(this).toBundle()
        )
    }

    // This code was removed since subjective collection was not being used this year.

//    override fun onBackPressed() {
//        AlertDialog.Builder(this).setMessage(R.string.error_back)
//            .setNegativeButton("OK") { _, _ -> TeamListActivity() }
//            .show()
//    }

//    override fun onKeyLongPress(keyCode: Int, event: KeyEvent): Boolean {
//        if (keyCode == KeyEvent.KEYCODE_BACK) {
//            AlertDialog.Builder(this).setMessage(R.string.error_back_reset)
//                .setPositiveButton("Yes") { _, _ -> intentToMatchInput() }
//                .setNegativeButton("No") { _, _ -> TeamListActivity() }
//                .show()
//        }
//        return super.onKeyLongPress(keyCode, event)
//    }


    override fun onResume() {
        super.onResume()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            lv_teams_list.adapter = TeamListAdapter(
                this,
                teamsList,
                retrieveFromStorage("mode_collection_select_activity"),
                lv_teams_list
            )
        }
    }
}
