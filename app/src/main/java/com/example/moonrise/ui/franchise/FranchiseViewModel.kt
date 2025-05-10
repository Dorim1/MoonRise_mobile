package com.example.moonrise.ui.franchise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.ContentWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class FranchiseViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val franchiseInfoDao = db.franchiseInfoDao()
    private val relatedContentDao = db.relatedContentDao()

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _contentList = MutableLiveData<List<ContentWithCategory>>()
    val contentList: LiveData<List<ContentWithCategory>> = _contentList

    fun loadFranchise(contentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val related = relatedContentDao.getRelated(contentId)
            val allIds = mutableSetOf(contentId)

            for (rc in related) {
                allIds.add(rc.relatedId)
                allIds.add(rc.contentId)
            }

            val franchiseId = allIds.minOrNull() ?: contentId

            val info = franchiseInfoDao.getFranchiseInfo(franchiseId)
            _description.postValue(info?.description ?: "Описание не найдено.")

            relatedContentDao.getRelatedContentWithCategory(contentId).collect { list ->
                _contentList.postValue(list)
            }
        }
    }
}