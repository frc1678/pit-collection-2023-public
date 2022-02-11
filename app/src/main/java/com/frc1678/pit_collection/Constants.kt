// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

class Constants {

    enum class ModeSelection {
        OBJECTIVE,
        SUBJECTIVE,
        NONE
    }

    data class DataObjective(
        var team_number: Int?,

        var drivetrain: Int?,
        var can_climb: Boolean?,
        var can_intake_terminal: Boolean?,
        var flag_cheesecake: Boolean?,
        var has_ground_intake: Boolean?,
        var can_under_low_rung: Boolean?,
        var can_cheesecake: Boolean?,
        var can_eject_terminal: Boolean?,
        var has_vision: Boolean?,

        //can cross trench and has ground intake deleted
        var drivetrain_motors: Int?,
        var drivetrain_motor_type: Int?
    )

    data class DataSubjective(
        var team_number: Int?,
        var climber_strap_installation_difficulty: Int?,
        var climber_strap_installation_notes: String?
    )
}