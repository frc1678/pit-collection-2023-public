// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.Manifest
import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.view.KeyEvent
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.android.synthetic.main.team_list_activity.*
import java.io.FileInputStream
import java.lang.reflect.Type

//Read the csv file, populate a listView, and start CollectionObjectiveDataActivity.
class TeamListActivity : CollectionActivity() {
    private var teamsList: List<String> = emptyList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.team_list_activity)
        setToolbarText(actionBar, supportActionBar)
    }

    fun jsonFileRead(): MutableList<String> {
        var teamListPath = "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/team_list.json"
        var teamList = FileInputStream(teamListPath).bufferedReader().use { it.readText() }
        val listType: Type = object : TypeToken<MutableList<String>>() {}.type

        return Gson().fromJson(teamList, listType)
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
        Permissions()
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            == PackageManager.PERMISSION_GRANTED
        ) {
            if (jsonFileRead() != ArrayList<String>()) {
                teamsList = jsonFileRead()
            }
            lv_teams_list.adapter = TeamListAdapter(
                this,
                teamsList,
                retrieveFromStorage("mode_collection_select_activity")
            )

            lv_teams_list.setOnItemClickListener { _, _, position, _ ->
                if (teamsList.isNotEmpty()) {
                    val element = teamsList[position]
                    val intent: Intent
//                    if (retrieveFromStorage("mode_collection_select_activity") == Constants.ModeSelection.OBJECTIVE.toString()
//                    ) {
                        intent = Intent(this, CollectionObjectiveDataActivity::class.java)
                        intent.putExtra("teamNumber", element)
                        startActivity(intent)
                    //}
//                    else if (retrieveFromStorage("mode_collection_select_activity") == Constants.ModeSelection.SUBJECTIVE.toString()
//                    ) {
//                        intent = Intent(this, CollectionSubjectiveActivity::class.java)
//                        intent.putExtra("teamNumber", element)
//                        startActivity(intent)
//                    }
                }
            }
        }
    }
}
