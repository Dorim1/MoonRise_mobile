package com.example.moonrise.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.RatingDao
import com.example.moonrise.data.local.dao.StatusDao

class ItemViewModelFactory(
    private val contentDao: ContentDao,
    private val statusDao: StatusDao,
    private val ratingDao: RatingDao
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ItemViewModel(contentDao, statusDao, ratingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}