// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.collection_objective_activity.*
import java.io.FileReader
import java.lang.Integer.parseInt

// TODO CONSOLIDATE INTO A SINGLE FUNCTION
fun objJsonFileRead(teamNum: Int?): Constants.DataObjective {
    val fileName = "/storage/emulated/0/Download/${teamNum}_obj_pit.json"

    // Make a json object called jo
    val obj = JsonParser().parse(FileReader(fileName))
    val jo = obj as JsonObject
    // Get values from the jo json file
    val drivetrainType = jo.get("drivetrain").asInt
    val has_communication_device = jo.get("has_communication_device").asBoolean
    val weight = jo.get("weight").asDouble
    val width = jo.get("width").asDouble
    val length = jo.get("length").asDouble
    val hasVision = jo.get("has_vision").asBoolean
    val numberOfDriveMotors = jo.get("drivetrain_motors").asInt
    val motorType = jo.get("drivetrain_motor_type").asInt

    // Create a DataObjective object with the information from jo

    return Constants.DataObjective(
        team_number = teamNum,
        drivetrain = drivetrainType,
        has_communication_device = has_communication_device,
        length = length,
        width = width,
        weight = weight,
        has_vision = hasVision,
        drivetrain_motors = numberOfDriveMotors,
        drivetrain_motor_type = motorType
    )
}

fun subjJsonFileRead(teamNum: Int?): Constants.DataSubjective {
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
