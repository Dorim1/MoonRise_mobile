package com.example.moonrise.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moonrise.data.local.entity.ContentWithCategory

class ProfileViewModel : ViewModel() {
    private val _filteredItems = MutableLiveData<List<ContentWithCategory>>() // замените на вашу модель
    val filteredItems: LiveData<List<ContentWithCategory>> = _filteredItems

    private var allItems: List<ContentWithCategory> = emptyList()

    fun setAllItems(items: List<ContentWithCategory>) {
        allItems = items
        _filteredItems.value = items
    }

    fun filterByStatus(status: String) {
        if (status == "Все") {
            _filteredItems.value = allItems
        } else {
            _filteredItems.value = allItems.filter { it.status == status }
        }
    }
}