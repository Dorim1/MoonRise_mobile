package com.example.moonrise.ui.item

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.entity.ContentWithCategory
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.dao.RatingDao
import com.example.moonrise.data.local.dao.StatusDao
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.Genre
import com.example.moonrise.data.local.entity.Rating
import com.example.moonrise.data.local.entity.Status
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class ItemViewModel(
    private val contentDao: ContentDao,
    private val statusDao: StatusDao,
    private val ratingDao: RatingDao
) : ViewModel() {

    fun getContent(contentId: Int): LiveData<ContentWithCategory> {
        return contentDao.getContentById(contentId).asLiveData()
    }

    fun getGenres(contentId: Int): LiveData<List<Genre>> {
        return contentDao.getGenresForContent(contentId).asLiveData()
    }

    fun getRelatedContent(contentId: Int): LiveData<List<ContentWithCategory>> {
        return contentDao.getRelatedByFranchise(contentId).asLiveData()
    }

    fun getStatus(contentId: Int): LiveData<Status?> {
        return statusDao.getStatus(contentId).asLiveData()
    }

    fun saveRating(contentId: Int, rating: Float) {
        viewModelScope.launch {
            ratingDao.insert(Rating(contentId = contentId, ratingValue = rating))
        }
    }

    fun getRating(contentId: Int): Flow<Rating?> {
        return ratingDao.getRatingForContent(contentId)
    }

    fun deleteRating(contentId: Int) {
        viewModelScope.launch {
            ratingDao.deleteByContentId(contentId)
        }
    }

}