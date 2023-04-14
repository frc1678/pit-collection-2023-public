// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.Manifest
import android.app.ActionBar
import android.content.pm.PackageManager
import android.os.Environment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import java.io.BufferedWriter
import java.io.FileWriter

open class CollectionActivity : AppCompatActivity() {
    fun populateScreenFromFile() {
        // TODO populate screen from data of specific team file
    }

    fun putIntoStorage(key: String, value: Any) {
        this.getSharedPreferences("PREFS", 0).edit().putString(key, value.toString()).apply()
    }

    fun retrieveFromStorage(key: String): String {
        return this.getSharedPreferences("PREFS", 0).getString(key, "").toString()
    }

    fun setToolbarText(view: ActionBar?, support: androidx.appcompat.app.ActionBar?) {
        when {
            retrieveFromStorage("mode_collection_select_activity") == Constants.ModeSelection.OBJECTIVE.toString() -> {
                view?.title = this.getString(R.string.event_key, Constants.EVENT_KEY) + System.getProperty("line.separator") + this.getString(R.string.version_num, version_num)
                support?.title = this.getString(R.string.event_key, Constants.EVENT_KEY) + System.getProperty("line.separator") + this.getString(R.string.version_num, version_num)
                view?.show()
                support?.show()
            }
            retrieveFromStorage("mode_collection_select_activity") == Constants.ModeSelection.SUBJECTIVE.toString() -> {
                view?.title = this.getString(R.string.tv_subjective_version, version_num)
                support?.title = this.getString(R.string.tv_subjective_version, version_num)
                view?.show()
                support?.show()
            }
            else -> {
                view?.title = this.getString(R.string.version_num, version_num)
                support?.title = this.getString(R.string.version_num, version_num)
                view?.show()
                support?.show()
            }
        }
    }

    fun convertToJson(teamData: Any?): String {
        return Gson().toJson(teamData)
    }

    fun writeToFile(fileName: String, jsonString: String) {
        val file = BufferedWriter(
            FileWriter(
                "/storage/self/primary/${Environment.DIRECTORY_DOWNLOADS}/$fileName.json", false
            )
        )
        file.write("$jsonString\n")
        file.close()
    }

    override fun onResume() {
        super.onResume()
        if ((ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            or (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED)
            or (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED)
        ) {
            try {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ),
                    100
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
