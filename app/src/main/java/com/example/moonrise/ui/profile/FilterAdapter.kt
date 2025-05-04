package com.example.moonrise.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R
import com.example.moonrise.data.local.entity.StatusWithCount

class FilterAdapter(
    private val filters: List<StatusWithCount>,
    private val onClick: (StatusWithCount) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.filter_name)
        val count: TextView = view.findViewById(R.id.filter_count)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.filter_item, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val item = filters[position]
        holder.name.text = item.name
        holder.count.text = item.count.toString()

        holder.itemView.setOnClickListener {
            onClick(item)
        }
    }

    override fun getItemCount(): Int = filters.size
}