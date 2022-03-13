// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import kotlinx.android.synthetic.main.collection_objective_activity.*

open class CollectionObjectiveActivity : CollectionActivity() {

    private var indexNumDrivetrain: Int? = null
    private var indexNumMotor: Int? = null

    // Create and populate a spinner.
    fun createSpinner(spinner: Spinner, array: Int) {
        ArrayAdapter.createFromResource(
            this, array, android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parentView: AdapterView<*>,
                selectedItemView: View,
                position: Int,
                id: Long
            ) {
                spinner.setSelection(position)
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {
                return
            }
        }
    }
//Puts datapoints in the intent passed out of the camera from the one originally passed into the camera
    fun putExtras(getIntent: Intent, intent: Intent, teamNum: String): Intent {
        intent.putExtra("teamNumber", teamNum)
            .putExtra("can_climb", getIntent.getBooleanExtra("can_climb", false))
            .putExtra("ground_intake", getIntent.getBooleanExtra("ground_intake", false))
            .putExtra("can_move_under_rung", getIntent.getBooleanExtra("can_move_under_rung",false))
            .putExtra("can_cheesecake", getIntent.getBooleanExtra("can_cheesecake", false))
            .putExtra("has_vision", getIntent.getBooleanExtra("has_vision", false))
            .putExtra("drivetrain_pos", getIntent.getIntExtra("drivetrain_pos", -1))
            .putExtra("drivetrain_motor_pos", getIntent.getIntExtra("drivetrain_motor_pos", -1))
            .putExtra("num_motors", getIntent.getIntExtra("num_motors",0))
        return intent
    }

    override fun onBackPressed() {}
}