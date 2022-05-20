// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.content.Intent
import android.os.Bundle
import kotlinx.android.synthetic.main.mode_collection_select_activity.*

class ModeCollectionSelectActivity : CollectionActivity() {
    private var modeSelection: Constants.ModeSelection = Constants.ModeSelection.NONE

    private fun startTeamList() {
        val intent = Intent(this, TeamListActivity::class.java)
        startActivity(intent)
    }

    private fun initButtonOnClicks() {
        btn_objective_collection_mode.setOnClickListener {
            modeSelection = Constants.ModeSelection.OBJECTIVE
            putIntoStorage("mode_collection_select_activity", modeSelection)
            startTeamList()
        }
        btn_subjective_collection_mode.setOnClickListener {
            modeSelection = Constants.ModeSelection.SUBJECTIVE
            putIntoStorage("mode_collection_select_activity", modeSelection)
            startTeamList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mode_collection_select_activity)
        setToolbarText(actionBar, supportActionBar)
        if (this.getSharedPreferences("PREFS", 0).contains("mode_collection_select_activity")) {
            startTeamList()
        } else {
            initButtonOnClicks()
        }
    }
}
