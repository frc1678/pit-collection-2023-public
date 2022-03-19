// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.Manifest
import android.app.ActivityOptions
import android.content.ClipboardManager
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.team_list_activity.*
import java.io.FileInputStream
import java.lang.reflect.Type
import android.content.ClipData
import android.content.Context
import android.util.Log
import com.google.gson.*
import java.io.File
import java.io.FileReader
import java.io.FileWriter
import com.google.gson.JsonArray





//Read the csv file, populate a listView, and start CollectionObjectiveDataActivity.
class TeamListActivity : CollectionActivity() {
    private var teamsList: List<String> = emptyList()
    companion object{
        var starredTeams: MutableSet<String> = mutableSetOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        StarredTeams.read(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.team_list_activity)
        setToolbarText(actionBar, supportActionBar)

        var mode = Constants.ModeSelection.OBJECTIVE
        putIntoStorage("mode_collection_select_activity", mode)
        for (team in StarredTeams.contents.toList()){
            starredTeams.add(team.asString)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu) : Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        val mapItem : MenuItem = menu.findItem(R.id.export_flags_button)
        val button = mapItem.actionView
        button.setOnClickListener {
            val flagsExport : MutableMap<String, List<String>> = mutableMapOf()
            for (team in teamsList){
                val flagsList : MutableList<String> = mutableListOf()
                if (File("/storage/emulated/0/Download/${team.toInt()}_obj_pit.json").exists()){
                    val teamJson = objJsonFileRead(team.toInt())
                    if((teamJson.drivetrain == 1) && (!flagsList.contains("Mechanum"))){
                        flagsList.add("Mechanum")
                    }
                    if((teamJson.has_ground_intake == false) && (!flagsList.contains("No Ground Intake"))){
                        flagsList.add("No Ground Intake")
                    }
                    if((teamJson.can_climb == false) && (!flagsList.contains("Can Not Climb"))){
                        flagsList.add("Can Not Climb")
                    }
                    if(flagsList.isNotEmpty()){
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

    fun jsonFileRead(): MutableList<String> {
        var teamListPath = "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/team_list.json"
        var teamList = FileInputStream(teamListPath).bufferedReader().use { it.readText() }
        val listType: Type = object : TypeToken<MutableList<String>>() {}.type

        return Gson().fromJson(teamList, listType)
    }

    object StarredTeams {

        var contents = JsonArray()
        var gson = Gson()

        private val file =
            File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/starred_teams.json")

        fun read(context: Context) {
//            if (!fileExists()) {
//                write()
//            }
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

    // Back button - put back in if using subjective pit collection
    // Restart app from ModeCollectionSelectActivity.kt when back button is long pressed.
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
            if (jsonFileRead() != ArrayList<String>()) {
                teamsList = jsonFileRead()
            }
            lv_teams_list.adapter = TeamListAdapter(
                this,
                teamsList,
                retrieveFromStorage("mode_collection_select_activity"),
                lv_teams_list
            )
        }
    }
}
