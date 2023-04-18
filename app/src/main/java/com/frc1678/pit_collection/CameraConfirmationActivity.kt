// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import kotlinx.android.synthetic.main.camera_confirmation_activity.*
import java.io.File

class CameraConfirmationActivity : CollectionObjectiveActivity() {
    private var fileName: String? = null
    private var teamNum: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_confirmation_activity)

        setToolbarText(actionBar, supportActionBar)

        teamNum = intent?.getStringExtra("teamNumber").toString()
        fileName = intent?.getStringExtra("fileName").toString()

        tv_team_number_confirm.text = teamNum
        tv_picture_type.text = intent?.getStringExtra("picture_type").toString().split(" ")
            .joinToString(separator = " ", transform = String::capitalize)

        displayImage(fileName!!)
        setOnClickListeners(teamNum.toString(), fileName.toString())
    }

    private fun rotateBitmap(source: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(
            source, 0, 0, source.width, source.height, matrix, true
        )
    }

    private fun displayImage(fileName: String) {
        val imgFile = File("$fileName.tmp")

        if (imgFile.exists()) {
            val myBitmap: Bitmap = BitmapFactory.decodeFile("$fileName.tmp")
            val rotatedBitmap = rotateBitmap(myBitmap, 90f)
            iv_picture_confirm.setImageBitmap(rotatedBitmap)
        }
    }

    private fun setOnClickListeners(teamNum: String, fileName: String) {
        delete.setOnClickListener {
            File("$fileName.tmp").delete()
            iv_picture_confirm.setImageBitmap(null)
            startActivity(
                putExtras(intent, Intent(this, CameraActivity::class.java), teamNum),
                ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    delete, "proceed_button"
                ).toBundle()
            )
        }
        btn_continue.setOnClickListener {
            File("$fileName.tmp").renameTo(File(fileName))
            iv_picture_confirm.setImageBitmap(null)
            startActivity(
                putExtras(intent, Intent(this, CameraActivity::class.java), teamNum),
                ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    btn_continue, "proceed_button"
                ).toBundle()
            )
        }
    }
}
