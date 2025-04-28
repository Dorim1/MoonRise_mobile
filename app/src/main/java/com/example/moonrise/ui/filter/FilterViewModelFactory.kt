package com.example.moonrise.ui.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.moonrise.data.local.dao.CategoryDao
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.GenreDao
import com.example.moonrise.data.local.dao.StatusTypeDao

class FilterViewModelFactory(
    private val genreDao: GenreDao,
    private val categoryDao: CategoryDao,
    private val contentDao: ContentDao,
    private val statusTypeDao: StatusTypeDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FilterViewModel::class.java)) {
            return FilterViewModel(genreDao, categoryDao, contentDao, statusTypeDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}