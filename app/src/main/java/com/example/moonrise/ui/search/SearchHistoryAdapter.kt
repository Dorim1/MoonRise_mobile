package com.example.moonrise.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R

class SearchHistoryAdapter(
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (SearchHistoryItem) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder>() {

    private val historyList = mutableListOf<SearchHistoryItem>()

    fun setHistory(newList: List<SearchHistoryItem>) {
        val diffCallback = SearchHistoryDiffCallback(historyList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        historyList.clear()
        historyList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun getItemCount() = historyList.size

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val item = historyList[position]
        holder.bind(item)
    }

    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(R.id.history_query_text)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.delete_history_button)

        fun bind(item: SearchHistoryItem) {
            textView.text = item.query
            itemView.setOnClickListener {
                onItemClick(item.query)
            }
            deleteButton.setOnClickListener {
                onDeleteClick(item)
            }
        }
    }
}