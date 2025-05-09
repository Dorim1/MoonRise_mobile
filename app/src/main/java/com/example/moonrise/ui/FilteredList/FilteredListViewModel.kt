package com.example.moonrise.ui.FilteredList

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.ContentWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FilteredListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()
    private val _filteredContent = MutableLiveData<List<ContentWithCategory>>()
    val filteredContent: LiveData<List<ContentWithCategory>> = _filteredContent

    fun applyFilters(
        genres: List<String>,
        category: String?,
        statusId: Int?,
        ageRating: String?,
        startYear: Int?,
        endYear: Int?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            val filtered = contentDao.getFilteredContentWithRelations(
                genres = genres,
                genresSize = genres.size,
                category = category,
                statusId = statusId,
                ageRating = ageRating,
                startYear = startYear,
                endYear = endYear
            ).first()

            _filteredContent.postValue(filtered)
        }
    }
}