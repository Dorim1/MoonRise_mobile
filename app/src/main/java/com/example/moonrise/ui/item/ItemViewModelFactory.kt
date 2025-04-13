package com.example.moonrise.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.RelatedContentDao

class ItemViewModelFactory(
    private val contentDao: ContentDao,
    private val relatedContentDao: RelatedContentDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(contentDao, relatedContentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}