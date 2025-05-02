package com.example.moonrise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ratings")
data class Rating(
    @PrimaryKey val contentId: Int,
    val ratingValue: Float
)