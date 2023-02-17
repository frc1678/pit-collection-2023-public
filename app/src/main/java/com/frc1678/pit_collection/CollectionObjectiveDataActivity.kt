// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.app.ActivityOptions
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.collection_objective_activity.*
import java.io.File
import java.lang.Double.parseDouble
import java.lang.Integer.parseInt
import java.util.*

// Create spinners (drivetrain and motor type).
class CollectionObjectiveDataActivity : CollectionObjectiveActivity(),
    AdapterView.OnItemSelectedListener {
    public var team_number: String? = null
    private var has_communication_device: Boolean? = null
    private var has_vision: Boolean? = null
    private var weight: Double? = null
    private var length: Double? = null
    private var width: Double? = null
    private var indexNumDrivetrain: Int? = null
    private var drivetrain: String? = null
    private var indexNumMotor: Int? = null
    private var drivetrain_motor_type: String? = null
    private var drivetrain_motors: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.collection_objective_activity)

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN)

        // Populate spinner with arrays from strings.xml
        createSpinner(spin_drivetrain, R.array.drivetrain_array)
        createSpinner(spin_drivetrain_motor_type, R.array.drivetrain_motor_type_array)

        team_number = intent.getStringExtra("teamNumber")!!
        tv_team_number.text = "$team_number"

        setToolbarText(actionBar, supportActionBar)

        cameraButton("$team_number")

        populateScreen()
    }

    override fun onItemSelected(parent: AdapterView<*>, view: View, pos: Int, id: Long) {
        parent.getItemAtPosition(pos)
    }

    private fun assignIndexNums() {
        // Use schemaRead() function to read pit_collection_schema.yml and use indexOf() to find corresponding enum value
        drivetrain = spin_drivetrain.selectedItem.toString().toLowerCase(Locale.US)

        indexNumDrivetrain = when (drivetrain) {
            "tank" -> {
                0
            }
            "mecanum" -> {
                1
            }
            "swerve" -> {
                2
            }
            "other" -> {
                3
            }
            else -> -1
        }

        drivetrain_motor_type =
            spin_drivetrain_motor_type.selectedItem.toString().toLowerCase(Locale.US)

        // Drive Train Motor Type
        // TODO: Hook up to enums instead of hard coding
        indexNumMotor = when (drivetrain_motor_type) {
            "minicim" -> {
                0
            }
            "cim" -> {
                1
            }
            "neo" -> {
                2
            }
            "falcon" -> {
                3
            }
            "other" -> {
                4
            }
            else -> -1
        }
    }

    private fun overLimit(): Boolean {
        if (et_length.text.toString() != "" && et_length.text.toString() != "." &&
            et_width.text.toString() != "" && et_width.text.toString() != "." &&
            et_weight.text.toString() != "" && et_weight.text.toString() != ".") {
            if (parseDouble(et_weight.text.toString()) > 125) {
                AlertDialog.Builder(this).setTitle("Error: Weight is above legal limit")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }.show()
            }
            if (parseDouble(et_length.text.toString()) > 120) {
                AlertDialog.Builder(this).setTitle("Error: Length is above legal limit")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }.show()
            }
            if (parseDouble(et_width.text.toString()) > 120) {
                AlertDialog.Builder(this).setTitle("Error: Width is above legal limit")
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.cancel()
                    }.show()
            }
            return ((parseDouble(et_weight.text.toString()) > 125) || (parseDouble(et_length.text.toString()) > 120)
                    || (parseDouble(et_width.text.toString()) > 120))
        } else {
            return false
        }
    }

    private fun cameraButton(teamNum: String) {
        btn_camera.setOnClickListener {
                assignIndexNums()

                val intent = Intent(this, CameraActivity::class.java)
                intent.putExtra("teamNumber", teamNum)
                    .putExtra("has_communication_device", tb_has_communication.isChecked)
                    .putExtra("has_vision", tb_has_vision.isChecked)
                    .putExtra("drivetrain_pos", parseInt(indexNumDrivetrain.toString()))
                    .putExtra("drivetrain_motor_pos", parseInt(indexNumMotor.toString()))
                if (et_weight.text.toString().isNotEmpty() && et_weight.text.toString() != ".") {
                    intent.putExtra("weight", parseDouble(et_weight.text.toString()))
                }
                else if (et_weight.text.toString() == ".") {
                    intent.putExtra("weight", 0.0)
                }
                if (et_length.text.toString().isNotEmpty() && et_length.text.toString() != ".") {
                    intent.putExtra("length", parseDouble(et_length.text.toString()))
                }
                else if (et_length.text.toString() == ".") {
                    intent.putExtra("length", 0.0)
                }
                if (et_width.text.toString().isNotEmpty() && et_width.text.toString() != ".") {
                    intent.putExtra("width", parseDouble(et_width.text.toString()))
                }
                else if (et_width.text.toString() == ".") {
                    intent.putExtra("width", 0.0)
                }
                if (et_number_of_motors.text.isNotEmpty()) {
                    intent.putExtra("num_motors", parseInt(et_number_of_motors.text.toString()))
                }
                startActivity(
                    intent, ActivityOptions.makeSceneTransitionAnimation(
                        this,
                        btn_camera, "proceed_button"
                    ).toBundle()
                )
            }
        }

    private fun populateScreen() {
        if (intent.getBooleanExtra("after_camera", false)) {
            tb_has_communication.isChecked = intent.getBooleanExtra("has_communication_device", false)
            tb_has_vision.isChecked = intent.getBooleanExtra("has_vision", false)
            if (intent.getDoubleExtra("weight", 0.0) != 0.0) {
                et_weight.setText(intent.getDoubleExtra("weight", 0.0).toString())
            }
            if (intent.getDoubleExtra("length", 0.0) != 0.0) {
                et_length.setText(intent.getDoubleExtra("length", 0.0).toString())
            }
            if (intent.getDoubleExtra("width", 0.0) != 0.0) {
                et_width.setText(intent.getDoubleExtra("width", 0.0).toString())
            }
            spin_drivetrain.setSelection(intent.getIntExtra("drivetrain_pos", -1) + 1)
            spin_drivetrain_motor_type.setSelection(intent.getIntExtra("drivetrain_motor_pos", -1) + 1)
            if (intent.getIntExtra("num_motors", 0) != 0) {
                et_number_of_motors.setText(intent.getIntExtra("num_motors", 0).toString())
            }
        } else if (File("/storage/emulated/0/Download/${team_number}_obj_pit.json").exists()) {
            val jsonFile = objJsonFileRead(team_number)
            tb_has_communication.isChecked = jsonFile.has_communication_device as Boolean
            tb_has_vision.isChecked = jsonFile.has_vision as Boolean
            if (jsonFile.weight.toString() != "0.0" && jsonFile.weight.toString() != ".") {
                et_weight.setText(jsonFile.weight.toString())
            }
            if (jsonFile.length.toString() != "0.0" && jsonFile.length.toString() != ".") {
                et_length.setText(jsonFile.length.toString())
            }
            if (jsonFile.width.toString() != "0.0" && jsonFile.width.toString() != ".") {
                et_width.setText(jsonFile.width.toString())
            }
            spin_drivetrain.setSelection(parseInt(jsonFile.drivetrain.toString()) + 1)
            spin_drivetrain_motor_type.setSelection(parseInt(jsonFile.drivetrain_motor_type.toString()) + 1)
            if (jsonFile.drivetrain_motors.toString() != "0") {
                et_number_of_motors.setText(jsonFile.drivetrain_motors.toString())
            }
        }
    }

    // Check if any changes are made to the obj data collection screen
    private fun allNotChecked(): Boolean {
        return (
            has_communication_device == false &&
            has_vision == false &&
                    (et_weight.text.toString() == "" || et_weight.text.toString() == ".") &&
                    (et_length.text.toString() == "" || et_length.text.toString() == ".") &&
                    (et_width.text.toString() == "" || et_width.text.toString() == ".") &&
            (indexNumDrivetrain == -1 || indexNumDrivetrain == null) &&
            (indexNumMotor == -1 || indexNumMotor == null) &&
            et_number_of_motors.text.toString() == ""
        )
    }

    private fun populateData() {
        has_communication_device = tb_has_communication.isChecked
        has_vision = tb_has_vision.isChecked
        weight = if (et_weight.text.toString() == "" || et_weight.text.toString() == ".") 0.0 else parseDouble(et_weight.text.toString())
        length = if (et_length.text.toString() == "" || et_length.text.toString() == ".") 0.0 else parseDouble(et_length.text.toString())
        width = if (et_width.text.toString() == "" || et_width.text.toString() == ".") 0.0 else parseDouble(et_width.text.toString())
        drivetrain_motors = if (et_number_of_motors.text.toString() == "") 0 else parseInt(et_number_of_motors.text.toString())
    }

    // Save obj data to a file in downloads
    private fun saver() {
        if(!overLimit()){

            populateData()
            // TODO Move below code to CollectionObjectiveDataActivity and link to save button

            assignIndexNums()

            // Save variable information as a pitData class.

            if (allNotChecked()) {
                val element = team_number
                val intent = Intent(this, TeamListActivity::class.java)
                intent.putExtra("teamNumber", element)
                startActivity(intent)
            } else {

                val information = Constants.DataObjective(
                    team_number = team_number,
                    has_communication_device = has_communication_device,
                    has_vision = has_vision,
                    weight = weight,
                    length = length,
                    width = width,
                    drivetrain = indexNumDrivetrain,
                    drivetrain_motor_type = indexNumMotor,
                    drivetrain_motors = drivetrain_motors
                )
                val jsonData = convertToJson(information)
                val fileName = "${team_number}_obj_pit"
                writeToFile(fileName, jsonData)
                val element = team_number
                val intent = Intent(this, TeamListActivity::class.java)
                intent.putExtra("teamNumber", element)
                startActivity(intent)
            }
        }
    }

    fun checksTeamInfo(team_number: String?, initial_run: Boolean) {
        // during initial run, et is not already pre-loaded, so we need to load/check data from json
        if (initial_run) {
            if (File("/storage/emulated/0/Download/${team_number}_obj_pit.json").exists()) {
                val jsonFile = objJsonFileRead(team_number)

                if (jsonFile.weight.toString().isNotEmpty() &&
                    jsonFile.length.toString().isNotEmpty() &&
                    jsonFile.width.toString().isNotEmpty() &&
                    jsonFile.weight.toString() != "0.0" &&
                    jsonFile.length.toString() != "0.0" &&
                    jsonFile.width.toString() != "0.0" &&
                    jsonFile.weight.toString() != "." &&
                    jsonFile.length.toString() != "." &&
                    jsonFile.width.toString() != "." &&
                    jsonFile.drivetrain.toString().isNotEmpty() &&
                    jsonFile.drivetrain_motor_type.toString().isNotEmpty() &&
                    jsonFile.drivetrain_motors.toString().isNotEmpty() &&
                    jsonFile.drivetrain_motors.toString() != "0") {
                    if (!teamsWithData.contains(team_number)) teamsWithData.add(team_number)
                } else teamsWithData.remove(team_number)
            }
        } else {
            if (et_weight.text.toString().isNotEmpty() &&
                et_length.text.toString().isNotEmpty() &&
                et_width.text.toString().isNotEmpty() &&
                et_weight.text.toString() != "0.0" &&
                et_length.text.toString() != "0.0" &&
                et_width.text.toString() != "0.0" &&
                et_weight.text.toString() != "." &&
                et_length.text.toString() != "." &&
                et_width.text.toString() != "." &&
                spin_drivetrain.selectedItem != "Drivetrain" &&
                spin_drivetrain_motor_type.selectedItem != "Drivetrain Motor Type" &&
                et_number_of_motors.text.isNotEmpty() &&
                et_number_of_motors.text.toString() != "0"
            ) {
                if (!teamsWithData.contains(team_number)) teamsWithData.add(team_number)
            } else teamsWithData.remove(team_number)
        }

        if ((File("/storage/emulated/0/Download/${team_number}_full_robot.jpg"
            ).exists()) && (File(
                "/storage/emulated/0/Download/${team_number}_front.jpg"
            ).exists()) && (File(
                "/storage/emulated/0/Download/${team_number}_side.jpg"
            ).exists())
        ) {
            if (!teamsWithPhotos.contains(team_number)) {
                teamsWithPhotos.add(team_number)
            }
        } else teamsWithPhotos.remove(team_number)
    }

    override fun onBackPressed() {

        when {
            et_weight.text.toString().isEmpty() || et_weight.text.toString() == "." -> {
                val weightSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Enter Weight",
                    Snackbar.LENGTH_SHORT
                )
                weightSnack.show()
            }
            et_length.text.toString().isEmpty() || et_length.text.toString() == "." -> {
                val lengthSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Enter Length",
                    Snackbar.LENGTH_SHORT
                )
                lengthSnack.show()
            }
            et_width.text.toString().isEmpty() || et_width.text.toString() == "." -> {
                val widthSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Enter Width",
                    Snackbar.LENGTH_SHORT
                )
                widthSnack.show()
            }
            spin_drivetrain.selectedItem.toString() == "Drivetrain" -> {

                val drivetrainSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Define A Drivetrain",
                    Snackbar.LENGTH_SHORT
                )
                drivetrainSnack.show()
            }
            spin_drivetrain_motor_type.selectedItem.toString() == "Drivetrain Motor Type" -> {
                val drivetrainMotorTypeSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Define A Drivetrain Motor Type",
                    Snackbar.LENGTH_SHORT
                )
                drivetrainMotorTypeSnack.show()
            }
            et_number_of_motors.text.isEmpty() -> {
                val numberOfMotorSnack = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Please Enter Number Of Drivetrain Motors",
                    Snackbar.LENGTH_SHORT
                )
                numberOfMotorSnack.show()
            }
            else -> {
                saver()
                checksTeamInfo(team_number, false)
            }
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>) {
        TODO("not implemented")
    }
    override fun onKeyLongPress(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            saver()
            checksTeamInfo(team_number, false)
            return true
        }
        return super.onKeyLongPress(keyCode, event)
    }
}
