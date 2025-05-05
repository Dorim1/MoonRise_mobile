package com.example.moonrise.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moonrise.data.local.entity.ContentWithCategory

class ProfileViewModel : ViewModel() {
    private val _filteredItems = MutableLiveData<List<ContentWithCategory>>()
    val filteredItems: LiveData<List<ContentWithCategory>> = _filteredItems

    private var allItems: List<ContentWithCategory> = emptyList()
    private var statusNameMap: Map<Int, String> = emptyMap()
    private var currentStatusFilter: String = "Все"
    private var currentQuery: String = ""

    fun setAllItems(items: List<ContentWithCategory>, nameMap: Map<Int, String>) {
        allItems = items
        statusNameMap = nameMap
        applyFilters()
    }

    fun searchItems(query: String) {
        currentQuery = query
        applyFilters()
    }

    fun filterByStatusName(statusName: String) {
        currentStatusFilter = statusName
        applyFilters()
    }

    private fun applyFilters() {
        val filteredByStatus = if (currentStatusFilter == "Все") {
            allItems.filter { it.status != null }
        } else {
            allItems.filter { item ->
                val typeName = item.status?.let { statusNameMap[it.statusTypeId] }
                typeName == currentStatusFilter
            }
        }

        val filteredByQuery = if (currentQuery.isNotBlank()) {
            filteredByStatus.filter { item ->
                item.content.title.contains(currentQuery, ignoreCase = true)
            }
        } else {
            filteredByStatus
        }

        _filteredItems.value = filteredByQuery
    }
}