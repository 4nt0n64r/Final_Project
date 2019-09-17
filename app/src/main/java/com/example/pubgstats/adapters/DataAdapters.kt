package com.example.pubgstats.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.pubgstats.Fragments.log
import com.example.pubgstats.R
import com.example.pubgstats.database.players.SimplePlayerUI
import com.example.pubgstats.database.seasons.SeasonUI
import kotlinx.android.synthetic.main.spinner_item.view.*


class PlayersAdapter(private var playersUIList: List<SimplePlayerUI>) :
    RecyclerView.Adapter<PlayersAdapter.ViewHolder>() {

    fun setData(items: List<SimplePlayerUI>) {
        this.playersUIList = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.name_player_item_layout, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.playerName?.text = playersUIList[position].name

        when (playersUIList[position].isSelected) {
            true -> {
                log("$playersUIList")
                holder.card!!.setCardBackgroundColor(Color.parseColor("#FFAB00"))// acc
                holder.playerName!!.setTextColor(Color.parseColor("#000000"))// pr dark
            }
            false -> {
                log("$playersUIList")
                holder.card!!.setCardBackgroundColor(Color.parseColor("#424242"))// bkg it
                holder.playerName!!.setTextColor(Color.parseColor("#FFAB00"))// acc
            }
        }
    }

    override fun getItemCount() = playersUIList.size

    class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        val playerName: TextView? = itemView?.findViewById(R.id.item_name)
        val card: CardView? = itemView?.findViewById(R.id.item_background)
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


