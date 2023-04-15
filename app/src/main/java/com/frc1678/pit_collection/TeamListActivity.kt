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
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonElement
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.pit_map_popup.view.*
import kotlinx.android.synthetic.main.team_cell.*
import kotlinx.android.synthetic.main.team_list_activity.lv_teams_list
import org.apache.commons.lang3.ObjectUtils.Null
import java.io.File
import java.io.FileReader
import java.io.FileWriter

// Read the csv file, populate a listView, and start CollectionObjectiveDataActivity.
class TeamListActivity : CollectionActivity() {
    var collectionObjectiveDataActivity = CollectionObjectiveDataActivity()

    companion object {
        var teamsList = emptyList<String>()
        var starredTeams: MutableSet<String> = mutableSetOf()
        var mapRotation = -90F
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

        for (team in teamsList) {
            collectionObjectiveDataActivity.checksTeamInfo(team, true)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.toolbar, menu)
        val mapItem: MenuItem = menu.findItem(R.id.pit_map)
        val pitMapButton = mapItem.actionView
        pitMapButton.setOnClickListener {
            val popupView = View.inflate(this, R.layout.pit_map_popup, null)
            val width = LinearLayout.LayoutParams.MATCH_PARENT
            val height = LinearLayout.LayoutParams.MATCH_PARENT
            val popupWindow = PopupWindow(popupView, width, height, false)

            var mapFile = File(
                Environment.getExternalStorageDirectory().resolve("Download"),
                "pit_map"
            )

            if (mapFile.exists()) {
                popupView.pit_map.setImageURI(mapFile.toUri())
            }

            mapRotation =
                this.getSharedPreferences("PREFS", 0).getFloat("mapRotation", mapRotation)

            popupView.pit_map.rotation = mapRotation
            popupWindow.showAtLocation(it, Gravity.CENTER, 0, 0)

            popupView.rotate_view.setOnClickListener {
                popupView.pit_map.rotation += 90F
                mapRotation = popupView.pit_map.rotation
                this.getSharedPreferences("PREFS", 0)?.edit()
                    ?.putFloat("mapRotation", mapRotation)?.apply()
            }

            popupView.close_button.setOnClickListener {
                popupWindow.dismiss()
            }
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
    // Not used this year as subjective collection is not being used
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
