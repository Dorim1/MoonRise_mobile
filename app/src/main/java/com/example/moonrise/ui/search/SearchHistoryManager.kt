package com.example.moonrise.ui.search

import android.content.Context
import androidx.core.content.edit
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SearchHistoryManager(context: Context) {
    private val prefs = context.getSharedPreferences("search_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveHistory(history: List<SearchHistoryItem>) {
        val json = gson.toJson(history)
        prefs.edit {
            putString("search_history", json)
        }
    }

    fun loadHistory(): List<SearchHistoryItem> {
        val json = prefs.getString("search_history", null)
        return if (json != null) {
            val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}