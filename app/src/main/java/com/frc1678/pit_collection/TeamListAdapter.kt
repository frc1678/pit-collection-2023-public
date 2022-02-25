// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import kotlinx.android.synthetic.main.team_cell.view.*
import java.io.File

class TeamListAdapter(
    private val context: Context,
    private val teamsList: List<String>,
    private val mode: String
) : BaseAdapter() {
    private var inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view = with(inflater) { inflate(R.layout.team_cell, parent, false) }
        view.team_number.text = teamsList[position]
        var allPics = (File(
                "/storage/emulated/0/Download/${teamsList[position]}_full_robot.jpg"
            ).exists()) && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_full_robot_2.jpg"
        ).exists() && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_drivetrain.jpg"
        ).exists()) && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_intake.jpg"
        ).exists()) && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_indexer.jpg"
        ).exists()) && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_shooter.jpg"
        ).exists()) && (File(
            "/storage/emulated/0/Download/${teamsList[position]}_climber.jpg"
        ).exists()))
        if (((mode == Constants.ModeSelection.OBJECTIVE.toString()) and (File(
                "/storage/emulated/0/Download/${teamsList[position]}_obj_pit.json"
            ).exists())
                    and allPics)
        ) {
            view.setBackgroundColor(context.resources.getColor(R.color.green, null))
        } else if (((mode == Constants.ModeSelection.OBJECTIVE.toString()) and (File(
                "/storage/emulated/0/Download/${teamsList[position]}_obj_pit.json"
            ).exists()))
        ) {
            view.setBackgroundColor(context.resources.getColor(R.color.light_orange, null))
        } else if ((mode == Constants.ModeSelection.OBJECTIVE.toString()) and allPics) {
            view.setBackgroundColor(context.resources.getColor(R.color.purple, null))
        }
        return view
    }

    override fun getItem(position: Int): Any {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return teamsList.size
    }
}