package com.example.moonrise.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "content",
    foreignKeys = [ForeignKey(entity = Category::class, parentColumns = ["id"], childColumns = ["categoryId"], onDelete = ForeignKey.CASCADE)]
)
data class Content(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val image: String,
    val title: String,
    val orTitle: String,
    val description: String,
    val categoryId: Int,
    val ageRating: String,
    val releaseDate: String
)