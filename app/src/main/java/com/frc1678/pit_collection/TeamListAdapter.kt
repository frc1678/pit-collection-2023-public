// Copyright (c) 2019 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.android.synthetic.main.team_cell.view.*
import java.io.File

class TeamListAdapter(
    private val context: Context,
    private val teamsList: List<String>,
    private val mode: String,
    private val listView: ListView
) : BaseAdapter() {
    private var inflater =
        context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        TeamListActivity.StarredTeams.read(context)
        val view = with(inflater) { inflate(R.layout.team_cell, parent, false) }
        view.team_number.text = teamsList[position]
        val allPics = (File(
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
        ).exists()))

        if (((mode == Constants.ModeSelection.OBJECTIVE.toString()) and (File(
                "/storage/emulated/0/Download/${teamsList[position]}_obj_pit.json"
            ).exists()) and allPics)
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

        if (TeamListActivity.starredTeams.contains(teamsList[position])) {
            view.star.setImageResource(R.drawable.yellow_star)
        } else{
            view.star.setImageResource(R.drawable.gray_star)
        }

        // Mark matches as starred when long clicked.
        view.setOnLongClickListener {
            Log.e("starred-team", teamsList[position])
            if (TeamListActivity.starredTeams.contains(teamsList[position])) {
                // The team is already starred.
                TeamListActivity.starredTeams.remove(teamsList[position])
                listView.invalidateViews()
            } else {
                // The team is not starred.
                TeamListActivity.starredTeams.add(teamsList[position])
                listView.invalidateViews()
            }
            for(i in 0 until TeamListActivity.StarredTeams.contents!!.size()){
                val size = TeamListActivity.StarredTeams.contents.size()
                Log.e("starred-size", "$size")
                TeamListActivity.StarredTeams.contents!!.remove(0)
            }
            for (team in TeamListActivity.starredTeams){
                TeamListActivity.StarredTeams.contents!!.add(team)
            }
            TeamListActivity.StarredTeams.write()
            TeamListActivity.StarredTeams.read(context)
            Log.e("starred", "${TeamListActivity.StarredTeams.contents}")

            return@setOnLongClickListener true
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