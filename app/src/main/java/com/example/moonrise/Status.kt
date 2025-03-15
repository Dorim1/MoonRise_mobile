package com.example.moonrise

import androidx.room.Entity

@Entity(tableName = "status", primaryKeys = ["contentId"])
data class Status (
    val contentId: Int,
    val status: String
)