// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

class Constants {

    enum class ModeSelection {
        OBJECTIVE,
        SUBJECTIVE,
        NONE
    }

    data class DataObjective(
        var team_number: String?,

        var drivetrain: Int?,
        var has_communication_device: Boolean?,
        var weight: Double?,
        var length: Double?,
        var width: Double?,
        var has_vision: Boolean?,

        // can cross trench and can eject terminal and can intake terminal deleted
        var drivetrain_motors: Int?,
        var drivetrain_motor_type: Int?
    )

    data class DataSubjective(
        var team_number: String?,
        var climber_strap_installation_difficulty: Int?,
        var climber_strap_installation_notes: String?
    )
}
