package com.example.moonrise.ui.list

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.Category
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.data.local.entity.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()
    private val statusDao = database.statusDao()
    private val categoryDao = database.categoryDao()

    val allContentWithCategory: LiveData<List<ContentWithCategory>> = contentDao.getAllContentWithCategory().asLiveData()

    fun addContent(vararg content: Content) {
        viewModelScope.launch {
            contentDao.insertAll(content.toList())
        }
    }

    fun setStatus(contentId: Int, status: String) {
        viewModelScope.launch {
            statusDao.insertStatus(Status(contentId, status))
        }
    }

    private fun saveContentList(contentList: List<Content>) {
        viewModelScope.launch {
            contentDao.insertAll(contentList)
        }
    }

    fun loadDataFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (contentDao.getContentCount() == 0) {
                val jsonString = context.assets.open("content.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val contentList: List<Content> = gson.fromJson(jsonString, object : TypeToken<List<Content>>() {}.type)

                saveContentList(contentList)
            }
        }
    }

    fun loadCategoriesFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories().firstOrNull()

            if (categories.isNullOrEmpty()) {
                val jsonString = context.assets.open("categories.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val categoryList: List<Category> = gson.fromJson(jsonString, object : TypeToken<List<Category>>() {}.type)

                categoryDao.insertCategory(categoryList)
            }
        }
    }
}