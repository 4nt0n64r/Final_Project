package com.example.final_project.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.database.seasons.SeasonUI
import com.example.final_project.database.players.SimplePlayer
import kotlinx.android.synthetic.main.spinner_item.view.*


class PlayersAdapter(private var players: List<SimplePlayer>) :
    RecyclerView.Adapter<PlayersAdapter.ViewHolder>() {

    fun setData(items: List<SimplePlayer>) {
        this.players = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.name_player_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playerName?.text = players[position].name
    }

    override fun getItemCount() = players.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val playerName: TextView? = itemView?.findViewById(R.id.item_name)
    }
}


class MySpinnerAdapter(ctx: Context, seasons: List<SeasonUI>) :
    ArrayAdapter<SeasonUI>(ctx, 0, seasons) {

    fun updateView() {
        notifyDataSetChanged()
    }

    override fun getView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    override fun getDropDownView(position: Int, recycledView: View?, parent: ViewGroup): View {
        return this.createView(position, recycledView, parent)
    }

    private fun createView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val season = getItem(position)
        val view = recycledView ?: LayoutInflater.from(context).inflate(
            R.layout.spinner_item,
            parent,
            false
        )
        view.season_name_tv.text = season.seasonName
        return view
    }
}


