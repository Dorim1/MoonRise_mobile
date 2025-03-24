package com.example.moonrise.data.local.entity

import androidx.room.Entity

@Entity(tableName = "content_genre", primaryKeys = ["contentId", "genreId"])
data class ContentGenre(
    val contentId: Int,
    val genreId: Int
)