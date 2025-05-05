package com.example.moonrise.ui.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.moonrise.R
import com.example.moonrise.data.local.entity.StatusWithCount

class FilterAdapter(
    private val filters: List<StatusWithCount>,
    private val onClick: (StatusWithCount) -> Unit
) : RecyclerView.Adapter<FilterAdapter.FilterViewHolder>() {

    private var selectedIndex = 0

    inner class FilterViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.filter_name)
        val count: TextView = view.findViewById(R.id.filter_count)
        val underline: View = view.findViewById(R.id.underline_view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_filter, parent, false)
        return FilterViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterViewHolder, position: Int) {
        val item = filters[position]
        val context = holder.itemView.context

        holder.name.text = item.name
        holder.count.text = item.count.toString()

        val isSelected = holder.bindingAdapterPosition == selectedIndex
        holder.name.setTextColor(
            ContextCompat.getColor(context, if (isSelected) android.R.color.white else android.R.color.darker_gray)
        )
        holder.count.setTextColor(
            ContextCompat.getColor(context, if (isSelected) android.R.color.white else android.R.color.darker_gray)
        )
        holder.underline.visibility = if (isSelected) View.VISIBLE else View.GONE

        holder.itemView.setOnClickListener {
            val prevIndex = selectedIndex
            selectedIndex = holder.bindingAdapterPosition

            if (prevIndex != RecyclerView.NO_POSITION) notifyItemChanged(prevIndex)
            if (selectedIndex != RecyclerView.NO_POSITION) notifyItemChanged(selectedIndex)

            onClick(item)
        }
    }

    override fun getItemCount(): Int = filters.size
}