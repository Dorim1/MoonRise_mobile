package com.example.moonrise

import androidx.recyclerview.widget.DiffUtil
import com.example.moonrise.data.local.entity.ContentWithCategory

class ContentDiffCallback(
    private val oldList: List<ContentWithCategory>,
    private val newList: List<ContentWithCategory>
) : DiffUtil.Callback() {

    override fun getOldListSize() = oldList.size
    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].content.id == newList[newItemPosition].content.id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}