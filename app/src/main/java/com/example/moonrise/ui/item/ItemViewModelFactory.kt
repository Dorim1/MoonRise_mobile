package com.example.moonrise.ui.item

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.data.local.dao.ContentDao

class ItemViewModelFactory(private val contentDao: ContentDao) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ItemViewModel::class.java)) {
            return ItemViewModel(contentDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}