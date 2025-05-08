package com.example.moonrise.ui.search

import androidx.recyclerview.widget.DiffUtil

class SearchHistoryDiffCallback(
    private val oldList: List<SearchHistoryItem>,
    private val newList: List<SearchHistoryItem>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].query == newList[newItemPosition].query
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}