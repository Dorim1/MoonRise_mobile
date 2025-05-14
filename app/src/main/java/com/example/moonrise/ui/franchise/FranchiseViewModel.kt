package com.example.moonrise.ui.franchise

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.data.local.database.AppDatabase
import com.example.moonrise.data.local.entity.ContentWithCategory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class FranchiseViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val franchiseInfoDao = database.franchiseInfoDao()
    private val contentDao = database.contentDao()

    private val _description = MutableLiveData<String>()
    val description: LiveData<String> = _description

    private val _contentList = MutableLiveData<List<ContentWithCategory>>()
    val contentList: LiveData<List<ContentWithCategory>> = _contentList

    fun loadFranchise(contentId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val franchiseId = franchiseInfoDao.getFranchiseIdForContent(contentId)

            if (franchiseId != null) {
                val info = franchiseInfoDao.getFranchiseInfo(franchiseId)
                _description.postValue(info?.description ?: "Описание не найдено.")

                val relatedContent = contentDao.getAllByFranchiseIncludingSelf(contentId).first()
                _contentList.postValue(relatedContent)
            } else {
                _description.postValue("Описание не найдено.")
                _contentList.postValue(emptyList())
            }
        }
    }
}