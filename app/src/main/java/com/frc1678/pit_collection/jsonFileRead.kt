// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.collection_objective_activity.*
import java.io.FileReader
import java.lang.Integer.parseInt

// TODO CONSOLIDATE INTO A SINGLE FUNCTION
fun objJsonFileRead(teamNum: String?): Constants.DataObjective {
    val fileName = "/storage/emulated/0/Download/${teamNum}_obj_pit.json"

    // Make a json object called jo
    val obj = JsonParser().parse(FileReader(fileName))
    val jo = obj as JsonObject
    // Get values from the jo json file
    val hasCommunicationDevice = jo.get("has_communication_device").asBoolean
    val hasVision = jo.get("has_vision").asBoolean
    val hasGroundIntake = jo.get("has_ground_intake")?.asBoolean
    val isForkable = jo.get("is_forkable")?.asBoolean
    val weight = jo.get("weight").asDouble
    val length = jo.get("length").asDouble
    val width = jo.get("width").asDouble
    val drivetrainType = jo.get("drivetrain").asInt
    val motorType = jo.get("drivetrain_motor_type").asInt
    val numberOfDriveMotors = jo.get("drivetrain_motors").asInt

    // Create a DataObjective object with the information from jo

    return Constants.DataObjective(
        team_number = teamNum,
        has_communication_device = hasCommunicationDevice,
        has_vision = hasVision,
        has_ground_intake = hasGroundIntake,
        is_forkable = isForkable,
        weight = weight,
        length = length,
        width = width,
        drivetrain = drivetrainType,
        drivetrain_motor_type = motorType,
        drivetrain_motors = numberOfDriveMotors
    )
}

fun subjJsonFileRead(teamNum: String?): Constants.DataSubjective {
    val fileName = "/storage/emulated/0/Download/${teamNum}_subj_pit.json"

    // Make a json object called jo
    val obj = JsonParser().parse(FileReader(fileName))
    val jo = obj as JsonObject

    // Get values from the jo json file
    val climber_strap_installation_difficulty = jo.get("climber_strap_installation_difficulty")
    val climber_strap_installation_notes = jo.get("climber_strap_installation_notes")

    return Constants.DataSubjective(
        teamNum,
        parseInt(climber_strap_installation_difficulty.toString()),
        climber_strap_installation_notes?.toString()
    )
}
