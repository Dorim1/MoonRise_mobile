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

    private lateinit var historyManager: SearchHistoryManager

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()

    private val _franchiseContent = MutableLiveData<List<ContentWithCategory>>()
    val franchiseContent: LiveData<List<ContentWithCategory>> = _franchiseContent

    private val _filteredContent = MutableLiveData<List<ContentWithCategory>>()
    val filteredContent: LiveData<List<ContentWithCategory>> = _filteredContent

    private val _searchHistory = MutableLiveData<List<SearchHistoryItem>>()
    val searchHistory: LiveData<List<SearchHistoryItem>> = _searchHistory

    private var history = mutableListOf<SearchHistoryItem>()

    fun searchContent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = if (query.isBlank()) {
                contentDao.getAllContentWithCategory().first()
            } else {
                contentDao.searchContentWithCategory(query).first()
            }
            _filteredContent.postValue(results)

            // Очистка франшизы, если строка запроса пуста
            if (query.isBlank()) {
                _franchiseContent.postValue(emptyList())
                return@launch
            }

            // Если найден хотя бы один элемент — ищем его franchise
            val firstMatch = results.firstOrNull()
            if (firstMatch != null) {
                val related = database.relatedContentDao()
                    .getRelatedContentWithCategory(firstMatch.content.id).first()
                _franchiseContent.postValue(related)
            } else {
                _franchiseContent.postValue(emptyList())
            }
        }
    }

    fun initManager(context: Context) {
        historyManager = SearchHistoryManager(context)
        history = historyManager.loadHistory().toMutableList()
        _searchHistory.postValue(history)
    }

    fun addQueryToHistory(query: String) {
        if (query.isNotBlank() && history.none { it.query == query }) {
            history.add(0, SearchHistoryItem(query))
            if (history.size > 10) history.removeAt(history.lastIndex)
            _searchHistory.postValue(history.toList())
            historyManager.saveHistory(history)
        }
    }




}