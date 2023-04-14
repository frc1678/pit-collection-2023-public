// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

object Constants {

    const val EVENT_KEY = "2023cada"
    const val GROSBEAK_AUTH_KEY = "02ae3a526cf54db9b563928b0ec05a77"

    enum class ModeSelection {
        OBJECTIVE,
        SUBJECTIVE,
        NONE
    }

    data class DataObjective(
        var team_number: String?,

        var drivetrain: Int?,
        var has_communication_device: Boolean?,
        var has_vision: Boolean?,
        var has_ground_intake: Boolean?,
        var is_forkable: Boolean?,
        var weight: Double?,
        var length: Double?,
        var width: Double?,

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
