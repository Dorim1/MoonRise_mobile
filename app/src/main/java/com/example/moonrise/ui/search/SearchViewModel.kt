package com.example.moonrise.ui.search

import android.app.Application
import android.content.Context
import androidx.core.content.edit
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

class SearchViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()

    private val _filteredContent = MutableLiveData<List<ContentWithCategory>>()
    val filteredContent: LiveData<List<ContentWithCategory>> = _filteredContent

    fun searchContent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = if (query.isBlank()) {
                contentDao.getAllContentWithCategory().first()
            } else {
                contentDao.searchContentWithCategory(query).first()
            }
            _filteredContent.postValue(results)
        }
    }

}