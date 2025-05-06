package com.example.moonrise.ui.search

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R

class SearchHistoryAdapter(
    private var history: List<String>,
    private val onItemClick: (String) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : RecyclerView.Adapter<SearchHistoryAdapter.HistoryViewHolder>() {

    class HistoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val queryText: TextView = view.findViewById(R.id.history_query_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_search_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val query = history[position]
        holder.queryText.text = query
        holder.itemView.setOnClickListener { onItemClick(query) }
        holder.itemView.findViewById<ImageButton>(R.id.delete_history_button).setOnClickListener {
            onDeleteClick(query)
        }
    }

    override fun getItemCount(): Int = history.size

    fun updateData(newHistory: List<String>) {
        val diffCallback = SearchHistoryDiffCallback(history, newHistory)
        val diffResult = DiffUtil.calculateDiff(diffCallback)

        history = newHistory
        diffResult.dispatchUpdatesTo(this)
    }
}