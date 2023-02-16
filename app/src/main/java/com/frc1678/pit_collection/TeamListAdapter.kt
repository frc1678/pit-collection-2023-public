// Copyright (c) 2022 FRC Team 1678: Citrus Circuits
package com.frc1678.pit_collection

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import androidx.core.content.ContextCompat.startActivity
import kotlinx.android.synthetic.main.collection_objective_activity.view.*
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

        // Read the file for each objective data file
        fun readFileDirectlyAsText(fileName: String): String {
            return if (File(fileName).exists()) {
                File(fileName).readText(Charsets.UTF_8)
            } else {
                "file doesn't exist"
            }
        }

        if ((teamsWithData.contains(teamsList[position])) && (teamsWithPhotos.contains(teamsList[position]))) {
            view.setBackgroundColor(context.resources.getColor(R.color.green, null))
        } else if ((teamsWithData.contains(teamsList[position]))) {
            view.setBackgroundColor(context.resources.getColor(R.color.light_orange, null))
        } else if ((teamsWithPhotos.contains(teamsList[position]))) {
            view.setBackgroundColor(context.resources.getColor(R.color.purple, null))
        }

        if (TeamListActivity.starredTeams.contains(teamsList[position])) {
            view.star.setImageResource(R.drawable.yellow_star)
        } else {
            view.star.setImageResource(R.drawable.gray_star)
        }

        view.setOnClickListener {
            if (teamsList.isNotEmpty()) {
                val element = teamsList[position]
                val intent = Intent(context, CollectionObjectiveDataActivity::class.java)
                intent.putExtra("teamNumber", element)
                startActivity(context, intent, Bundle())
            }
        }

        // Mark teams as starred when long clicked.
        view.setOnLongClickListener {
            if (TeamListActivity.starredTeams.contains(teamsList[position])) {
                // The team is already starred.
                TeamListActivity.starredTeams.remove(teamsList[position])
                listView.invalidateViews()
            } else {
                // The team is not starred.
                TeamListActivity.starredTeams.add(teamsList[position])
                listView.invalidateViews()
            }
            for (i in 0 until TeamListActivity.StarredTeams.contents.size()) {
                TeamListActivity.StarredTeams.contents.remove(0)
            }
            for (team in TeamListActivity.starredTeams) {
                TeamListActivity.StarredTeams.contents.add(team)
            }
            TeamListActivity.StarredTeams.write()
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
