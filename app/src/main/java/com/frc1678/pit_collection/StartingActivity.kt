package com.frc1678.pit_collection

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.core.content.ContextCompat
import java.io.File

/* This starting activity opens up a white page and brings a warning pop-up
   if the teams list file doesn't exist.
   Otherwise, goes to TeamListActivity.kt

 */
class StartingActivity: CollectionActivity() {

    // Starts the activity
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.starting_activity)
        teamListExists()
    }

    // Checks if the teams list file exists
    fun teamListExists(){
        val teamListFile = File("/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/team_list.json")

        if(teamListFile.exists()){

            // If the teams_list file exist, proceed onto TeamListActivity.kt
            ContextCompat.startActivity(
                this,
                Intent(this, TeamListActivity::class.java),
                null
            )
        }


        }
    }



