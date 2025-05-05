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

    fun setAllItems(items: List<ContentWithCategory>, nameMap: Map<Int, String>) {
        allItems = items
        statusNameMap = nameMap
        _filteredItems.value = items
    }

    fun filterByStatusName(statusName: String) {
        val filtered = if (statusName == "Все") {
            allItems.filter { item ->
                item.status?.let { statusNameMap[it.statusTypeId] } != null
            }
        } else {
            allItems.filter { item ->
                val typeName = item.status?.let { statusNameMap[it.statusTypeId] }
                typeName == statusName
            }
        }
        _filteredItems.value = filtered
    }
}