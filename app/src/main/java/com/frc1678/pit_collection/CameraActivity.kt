// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.app.ActivityOptions
import android.content.Intent
import android.graphics.Matrix
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.camera.core.*
import androidx.core.content.ContextCompat.getColor
import androidx.lifecycle.LifecycleOwner
import kotlinx.android.synthetic.main.camera_preview_activity.*
import java.io.File
import java.util.*
import java.util.concurrent.Executors

class CameraActivity : CollectionObjectiveActivity(), LifecycleOwner {
    private lateinit var teamNum: String
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var viewFinder: TextureView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.camera_preview_activity)

        setToolbarText(actionBar, supportActionBar)

        teamNum = intent.getStringExtra("teamNumber")!!.toString()

        finishButton(teamNum)

        viewFinder = findViewById(R.id.view_finder)

        // Set the team number for the picture
        viewFinder.post { startCamera(teamNum) }

        // Every time the provided texture view changes, recompute layout
        viewFinder.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
            updateTransform()
        }

        picturesTaken()
    }

    // changes the color of buttons in camera_preview_activity to green if the picture has already been taken and grey otherwise
    // This should probably be a for loop, but I could figure out how to change the ID of the button as well without making a second function.
    fun picturesTaken() {
        if (File("/storage/emulated/0/Download/${teamNum}_full_robot_1.jpg").exists()) {
            full_robot_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            full_robot_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_full_robot_2.jpg").exists()) {
            full_robot_2_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            full_robot_2_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_drivetrain.jpg").exists()) {
            drivetrain_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            drivetrain_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_intake.jpg").exists()) {
            intake_picture_type.setBackgroundColor(getColor(this, R.color.dark_green))
        } else {
            intake_picture_type.setBackgroundColor(getColor(this, R.color.dark_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_indexer.jpg").exists()) {
            indexer_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            indexer_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_shooter.jpg").exists()) {
            shooter_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            shooter_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }

        if (File("/storage/emulated/0/Download/${teamNum}_climber.jpg").exists()) {
            climber_picture_type.setBackgroundColor(getColor(this, R.color.green))
        } else {
            climber_picture_type.setBackgroundColor(getColor(this, R.color.light_gray))
        }
    }

    private fun finishButton(teamNum: String) {
        btn_return.setOnClickListener {
            startActivity(
                putExtras(
                    intent,
                    Intent(
                        this,
                        CollectionObjectiveDataActivity::class.java
                    ).putExtra("after_camera", true), teamNum
                ),
                ActivityOptions.makeSceneTransitionAnimation(
                    this,
                    btn_return, "proceed_button"
                ).toBundle()
            )
        }
    }

    private fun startCamera(teamNum: String) {
        // Create configuration object for the viewfinder use case
        val previewConfig = PreviewConfig.Builder().apply {
            setTargetResolution(Size(640, 480))
        }.build()


        // Build the viewfinder use case
        val preview = Preview(previewConfig)

        // Every time the viewfinder is updated, recompute layout
        preview.setOnPreviewOutputUpdateListener {

            // Updates the SurfaceTexture by removing it and re-adding it
            val parent = viewFinder.parent as ViewGroup
            parent.removeView(viewFinder)
            parent.addView(viewFinder, 0)

            viewFinder.surfaceTexture = it.surfaceTexture
            updateTransform()
        }

        // Create configuration object for the image capture use case
        val imageCaptureConfig = ImageCaptureConfig.Builder()
            .apply {
                // Select a capture mode which will infer the appropriate
                // resolution based on aspect ration and requested mode
                setCaptureMode(ImageCapture.CaptureMode.MIN_LATENCY)
            }.build()

        // Build the image capture use case and attach button click listener
        val imageCapture = ImageCapture(imageCaptureConfig)

        // List of all of the picture type options
        val listOfPictureTypes = listOf(intake_picture_type, indexer_picture_type, shooter_picture_type,
            climber_picture_type, full_robot_picture_type, full_robot_2_picture_type, drivetrain_picture_type)

        // Set default picture type to intake
        var pictureTypeId = intake_picture_type

        // Set the picture type to whatever picture type button the user clicks on
        // If the
        fun setPictureType (buttonId : Button) {
            buttonId.setOnClickListener {
                if(File("/storage/emulated/0/Download/${teamNum}_${pictureTypeId.text.toString().
                    toLowerCase(Locale.US)}.jpg").exists()) {
                    pictureTypeId.setBackgroundColor((getColor(this, R.color.green)))
                } else {
                    pictureTypeId.setBackgroundColor((getColor(this, R.color.light_gray)))
                }

                if(File("/storage/emulated/0/Download/${teamNum}_${buttonId.text.toString().
                    toLowerCase(Locale.US)}.jpg").exists()) {
                    buttonId.setBackgroundColor(getColor(this, R.color.dark_green))
                } else {
                    buttonId.setBackgroundColor(getColor(this, R.color.dark_gray))
                }

                pictureTypeId = buttonId
            }
        }

        // Run the setPictureType function for every picture type in listOfPictureTypes
        for (type in listOfPictureTypes) {
            setPictureType(type)
        }

        // Take a picture and gives it the correct file name based on team number and picture type
        // If the picture fails it will give a Photo capture failed message
        // Save picture to File Downloads
        capture_button.setOnClickListener {
            // Create file name based on team number and picture type
            var pictureType = pictureTypeId.text.toString().toLowerCase(Locale.US)
            val fileName = "${teamNum}_${formatPictureType(pictureType)}"
            val filepath = "/storage/emulated/0/${Environment.DIRECTORY_DOWNLOADS}/$fileName.jpg"

            imageCapture.takePicture(File("$filepath.tmp"), executor,
                object : ImageCapture.OnImageSavedListener {
                    override fun onError(
                        imageCaptureError: ImageCapture.ImageCaptureError,
                        message: String,
                        exc: Throwable?
                    ) {
                        val msg = "Photo capture failed: $message"
                        viewFinder.post {
                            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onImageSaved(file: File) {
                        this@CameraActivity.runOnUiThread {
                            startActivity(
                                putExtras(
                                    intent,
                                    Intent(
                                        this@CameraActivity,
                                        CameraConfirmationActivity::class.java
                                    ),
                                    teamNum
                                )
                                    .putExtra("fileName", filepath)
                                    .putExtra("picture_type", pictureType),
                                ActivityOptions.makeSceneTransitionAnimation(
                                    this@CameraActivity,
                                    capture_button, "proceed_button"
                                ).toBundle()
                            )
                        }
                    }
                })
        }

        // Bind use cases to lifecycle
        // If Android Studio complains about "this" being not a LifecycleOwner
        // try rebuilding the project or updating the appcompat dependency to
        // version 1.1.0 or higher.
        CameraX.bindToLifecycle(this, preview, imageCapture)
    }

    // Delete the space in the "full robot" picture type, replace it with an "_"
    private fun formatPictureType(pictureType: String): String {
        val pictureName: String
        return if (pictureType == "full robot 1") {
            pictureName = "full_robot_1"
            pictureName
        } else if (pictureType == "full robot 2") {
            pictureName = "full_robot_2"
            pictureName
        } else {
            pictureType
        }
    }

    private fun updateTransform() {
        val matrix = Matrix()

        // Compute the center of the view finder
        val centerX = viewFinder.width / 2f
        val centerY = viewFinder.height / 2f

        // Correct preview output to account for display rotation
        val rotationDegrees = when (viewFinder.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)

        // Apply transformations to our TextureView
        viewFinder.setTransform(matrix)
    }
}
