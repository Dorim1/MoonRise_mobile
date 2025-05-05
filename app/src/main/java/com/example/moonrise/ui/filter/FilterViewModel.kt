package com.example.moonrise.ui.filter

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.liveData
import com.example.moonrise.data.local.dao.CategoryDao
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.GenreDao
import com.example.moonrise.data.local.dao.StatusDao
import com.example.moonrise.data.local.dao.StatusTypeDao
import com.example.moonrise.data.local.entity.StatusType

class FilterViewModel(
    private val genreDao: GenreDao,
    private val categoryDao: CategoryDao,
    private val contentDao: ContentDao,
    private val statusTypeDao: StatusTypeDao
) : ViewModel() {

    val genres = genreDao.getAllGenres().asLiveData()
    val categories = categoryDao.getAllCategories().asLiveData()
    val ageRatings = contentDao.getAllAgeRatings().asLiveData()
    val statusTypes = statusTypeDao.observeAllStatusTypes().asLiveData()

    fun getYearRange(): LiveData<Pair<Int?, Int?>> = liveData {
        val yearRange = contentDao.getYearRange()
        emit(yearRange)
    }

    var selectedGenres = mutableSetOf<String>()
    var selectedStatusId: Int? = null
    var selectedCategory: String? = null
    var selectedAgeRating: String? = null
    var selectedStartYear: Int? = null
    var selectedEndYear: Int? = null

}
