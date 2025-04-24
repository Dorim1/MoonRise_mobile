package com.example.moonrise.ui.item

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.entity.ContentWithCategory
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.dao.RelatedContentDao
import com.example.moonrise.data.local.dao.StatusDao
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.Genre
import com.example.moonrise.data.local.entity.RelatedContent
import com.example.moonrise.data.local.entity.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ItemViewModel(
    private val contentDao: ContentDao,
    private val relatedContentDao: RelatedContentDao,
    private val statusDao: StatusDao
) : ViewModel() {

    fun getContent(contentId: Int): LiveData<ContentWithCategory> {
        return contentDao.getContentById(contentId).asLiveData()
    }

    fun getGenres(contentId: Int): LiveData<List<Genre>> {
        return contentDao.getGenresForContent(contentId).asLiveData()
    }

    fun getRelatedContent(contentId: Int): LiveData<List<ContentWithCategory>> {
        return relatedContentDao.getRelatedContentWithCategory(contentId).asLiveData()
    }

    fun getStatus(contentId: Int): LiveData<Status?> {
        return statusDao.getStatus(contentId).asLiveData()
    }

    fun loadRelatedContentFromJson(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val existing = relatedContentDao.getRelated(1)
            if (existing.isEmpty()) {
                val jsonString = context.assets.open("related_content.json").bufferedReader().use { it.readText() }
                val gson = Gson()
                val relatedList: List<RelatedContent> = gson.fromJson(jsonString, object : TypeToken<List<RelatedContent>>() {}.type)
                relatedContentDao.insertAll(relatedList)
            }
        }
    }
}