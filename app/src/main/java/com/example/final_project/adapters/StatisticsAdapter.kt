package com.example.final_project.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.final_project.R
import com.example.final_project.StatPoints
import com.example.final_project.StatHeader
import com.example.final_project.StatList
import com.hannesdorfmann.adapterdelegates4.AbsListItemAdapterDelegate
import com.hannesdorfmann.adapterdelegates4.ListDelegationAdapter

class StatisticsAdapter : ListDelegationAdapter<List<StatList>>() {

    init {
        delegatesManager.addDelegate(StatElemDelegate())
        delegatesManager.addDelegate(DescriptionElemDelegate())
    }

    fun setData(data: List<StatList>) {
        this.items = data
        notifyDataSetChanged()
    }
}

private class StatElemDelegate :
    AbsListItemAdapterDelegate<StatPoints, StatList, StatElemDelegate.ViewHolder>() {

    override fun isForViewType(item: StatList, items: List<StatList>, position: Int): Boolean {
        return item is StatPoints
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.statistics_item,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(item: StatPoints, viewHolder: ViewHolder, payloads: List<Any>) {
        viewHolder.statText.text = item.text
        viewHolder.statPoints.text = item.points
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val statText: TextView = itemView.findViewById(R.id.stat_text)
        val statPoints: TextView = itemView.findViewById(R.id.stat_points)
    }
}

private class DescriptionElemDelegate :
    AbsListItemAdapterDelegate<StatHeader, StatList, DescriptionElemDelegate.ViewHolder>() {

    override fun isForViewType(item: StatList, items: List<StatList>, position: Int): Boolean {
        return item is StatHeader
    }

    override fun onCreateViewHolder(parent: ViewGroup): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.stat_description_layout,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(item: StatHeader, viewHolder: ViewHolder, payloads: List<Any>) {
        viewHolder.descText.text = item.text
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val descText: TextView = itemView.findViewById(R.id.description_tv)
    }
}
