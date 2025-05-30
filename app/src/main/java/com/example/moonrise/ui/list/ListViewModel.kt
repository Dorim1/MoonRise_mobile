package com.example.moonrise.ui.list

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.Category
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.ContentGenre
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.data.local.entity.FranchiseInfo
import com.example.moonrise.data.local.entity.Genre
import com.example.moonrise.data.local.entity.StatusType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()
    private val statusTypeDao = database.statusTypeDao()
    private val categoryDao = database.categoryDao()
    private val genreDao = database.genreDao()
    private val contentGenreDao = database.contentGenreDao()
    private val _filteredContent = MutableLiveData<List<ContentWithCategory>>()
    private val franchiseInfoDao = database.franchiseInfoDao()

    private var randomizedContent: List<ContentWithCategory>? = null
    private var isShuffled = false
    private var currentContentList: List<ContentWithCategory> = emptyList()

    val filteredContent: LiveData<List<ContentWithCategory>> = _filteredContent


     private fun observeAllContent() {
        viewModelScope.launch {
            contentDao.getAllContentWithCategory()
                .collectLatest { list ->

                    if (!isShuffled) {
                        currentContentList = list.shuffled()
                        isShuffled = true
                    } else {
                        val updatedMap = list.associateBy { it.content.id }
                        currentContentList = currentContentList.mapNotNull { old ->
                            updatedMap[old.content.id]
                        }
                    }

                    _filteredContent.postValue(currentContentList)
                }
        }
    }

    fun searchContent(query: String) {
        viewModelScope.launch {
            if (query.isBlank()) {
                _filteredContent.postValue(currentContentList)
            } else {
                contentDao.searchContentWithCategory(query)
                    .collectLatest { searchResults ->
                        _filteredContent.postValue(searchResults)
                    }
            }
        }
    }

    fun refreshContent() {
        randomizedContent?.let {
            _filteredContent.postValue(it)
        }
    }

    private fun saveContentList(contentList: List<Content>) {
        viewModelScope.launch {
            contentDao.insertAll(contentList)
        }
    }

    private fun loadDataFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            if (contentDao.getContentCount() == 0) {
                val jsonString =
                    context.assets.open("content.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val contentList: List<Content> =
                    gson.fromJson(jsonString, object : TypeToken<List<Content>>() {}.type)

                saveContentList(contentList)
            }
        }
    }

    private fun loadCategoriesFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val categories = categoryDao.getAllCategories().firstOrNull()

            if (categories.isNullOrEmpty()) {
                val jsonString =
                    context.assets.open("categories.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val categoryList: List<Category> =
                    gson.fromJson(jsonString, object : TypeToken<List<Category>>() {}.type)

                categoryDao.insertCategory(categoryList)
            }
        }
    }

    private fun loadGenresFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val genres = genreDao.getAllGenres().firstOrNull()

            if (genres.isNullOrEmpty()) {
                val jsonString =
                    context.assets.open("genres.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val genreList: List<Genre> =
                    gson.fromJson(jsonString, object : TypeToken<List<Genre>>() {}.type)

                genreDao.insertGenreList(genreList)
            }
        }
    }

    private fun loadContentGenresFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val someContentGenre = contentGenreDao.getContentByGenre(1).firstOrNull()

            if (someContentGenre.isNullOrEmpty()) {
                val jsonString =
                    context.assets.open("content_genre.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val contentGenreList: List<ContentGenre> =
                    gson.fromJson(jsonString, object : TypeToken<List<ContentGenre>>() {}.type)

                contentGenreList.forEach { contentGenreDao.insert(it) }
            }
        }
    }

    private fun loadStatusTypesFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val statusTypes = statusTypeDao.getAllStatusTypes()

            if (statusTypes.isEmpty()) {
                val jsonString =
                    context.assets.open("status_types.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val statusTypeList: List<StatusType> =
                    gson.fromJson(jsonString, object : TypeToken<List<StatusType>>() {}.type)

                statusTypeDao.insertAll(statusTypeList)
            }
        }
    }

    private fun loadFranchiseInfoFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = franchiseInfoDao.getFranchiseInfo(100) // или franchiseInfoDao.getAll().firstOrNull()
            if (existing == null) {
                val jsonString =
                    context.assets.open("franchises.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val list: List<FranchiseInfo> =
                    gson.fromJson(jsonString, object : TypeToken<List<FranchiseInfo>>() {}.type)
                franchiseInfoDao.insertAll(list)
            }
        }
    }


    fun checkAndInitDatabase(context: Context, onFinished: () -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            if (contentDao.getContentCount() == 0) {
                loadDataFromJson(context)
                loadCategoriesFromJson(context)
                loadGenresFromJson(context)
                loadContentGenresFromJson(context)
                loadStatusTypesFromJson(context)
                loadFranchiseInfoFromJson(context)
            }

            while (contentDao.getContentCount() == 0) {
                kotlinx.coroutines.delay(100)
            }

            observeAllContent() // <<<<< переместили сюда
            onFinished()
        }
    }

}