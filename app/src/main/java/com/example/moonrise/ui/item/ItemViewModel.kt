package com.example.moonrise.ui.item

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.entity.ContentWithCategory
import androidx.lifecycle.asLiveData
import com.example.moonrise.data.local.entity.Genre

class ItemViewModel(private val contentDao: ContentDao) : ViewModel() {

    fun getContent(contentId: Int): LiveData<ContentWithCategory> {
        return contentDao.getContentById(contentId).asLiveData()
    }

    fun getGenres(contentId: Int): LiveData<List<Genre>> {
        return contentDao.getGenresForContent(contentId).asLiveData()
    }

}