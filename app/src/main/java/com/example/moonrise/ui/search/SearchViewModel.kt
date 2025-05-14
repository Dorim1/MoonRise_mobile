package com.example.moonrise.ui.search

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
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
    private var lastFranchiseContentId: Int? = null

    private var _currentQuery: String = ""
    val currentQuery: String
        get() = _currentQuery

    fun setCurrentQuery(query: String) {
        _currentQuery = query
    }

    fun getLastFranchiseContentId(): Int? = lastFranchiseContentId

    fun searchContent(query: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val results = if (query.isBlank()) {
                contentDao.getAllContentWithCategory().first()
            } else {
                contentDao.searchContentWithCategory(query).first()
            }
            _filteredContent.postValue(results)

            if (query.isBlank()) {
                _franchiseContent.postValue(emptyList())
                return@launch
            }

            val firstMatch = results.firstOrNull()
            if (firstMatch != null) {
                lastFranchiseContentId = firstMatch.content.id

                val related = contentDao.getRelatedByFranchise(firstMatch.content.id).first()
                    .filter { it.content.id != firstMatch.content.id } // убрать дубликат, если вдруг есть

                val fullFranchiseList = listOf(firstMatch) + related

                _franchiseContent.postValue(fullFranchiseList)
            } else {
                lastFranchiseContentId = null
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
        if (query.isBlank()) return

        history.removeAll { it.query == query }

        history.add(0, SearchHistoryItem(query))

        if (history.size > 10) {
            history = history.take(10).toMutableList()
        }

        _searchHistory.postValue(history.toList())
        historyManager.saveHistory(history)
    }

    fun removeQueryFromHistory(item: SearchHistoryItem) {
        history.remove(item)
        _searchHistory.postValue(history.toList())
        historyManager.saveHistory(history)
    }

}