package com.example.lotteryprediction.deepseek.task

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.lotteryprediction.R

class ReportHistoryAdapter(
    private val onItemClick: (String) -> Unit
) : ListAdapter<Pair<String, String>, ReportHistoryAdapter.ViewHolder>(DiffCallback()) {

    class ViewHolder(view: View, private val onItemClick: (String) -> Unit) : 
        RecyclerView.ViewHolder(view) {
        private val titleView: TextView = view.findViewById(R.id.history_item_title)
        private var currentContent: String? = null

        init {
            view.setOnClickListener {
                currentContent?.let(onItemClick)
            }
        }

        fun bind(item: Pair<String, String>) {
            titleView.text = item.first.replace("report_", "").replace(".txt", "")
            currentContent = item.second
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_report_history, parent, false)
        return ViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    private class DiffCallback : DiffUtil.ItemCallback<Pair<String, String>>() {
        override fun areItemsTheSame(
            oldItem: Pair<String, String>, 
            newItem: Pair<String, String>
        ) = oldItem.first == newItem.first

        override fun areContentsTheSame(
            oldItem: Pair<String, String>, 
            newItem: Pair<String, String>
        ) = oldItem == newItem
    }
}
