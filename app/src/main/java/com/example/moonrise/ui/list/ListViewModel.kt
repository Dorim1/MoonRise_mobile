package com.example.moonrise.ui.list

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.moonrise.AppDatabase
import com.example.moonrise.Content
import com.example.moonrise.Status
import kotlinx.coroutines.launch

class ListViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val contentDao = database.contentDao()
    private val statusDao = database.statusDao()

    val allContent: LiveData<List<Content>> = contentDao.getAllContent().asLiveData()

    fun addContent(content: Content) {
        viewModelScope.launch {
            contentDao.insertContent(content)
        }
    }

    fun setStatus(contentId: Int, status: String) {
        viewModelScope.launch {
            statusDao.insertStatus(Status(contentId, status))
        }
    }
}