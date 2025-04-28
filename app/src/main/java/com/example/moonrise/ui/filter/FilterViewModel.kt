package com.example.moonrise.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.moonrise.data.local.dao.CategoryDao
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.GenreDao

class FilterViewModel(
    private val genreDao: GenreDao,
    private val categoryDao: CategoryDao,
    private val contentDao: ContentDao
) : ViewModel() {

    val genres = genreDao.getAllGenres().asLiveData()
    val categories = categoryDao.getAllCategories().asLiveData()
    val ageRatings = contentDao.getAllAgeRatings().asLiveData()

    fun getYearRange(): LiveData<Pair<Int?, Int?>> = liveData {
        val yearRange = contentDao.getYearRange()
        emit(yearRange)
    }
}