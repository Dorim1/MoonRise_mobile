package com.example.moonrise

import androidx.recyclerview.widget.DiffUtil
import com.example.moonrise.data.local.entity.Content

class ContentDiffCallback(
    private val oldList: List<Content>,
    private val newList: List<Content>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}