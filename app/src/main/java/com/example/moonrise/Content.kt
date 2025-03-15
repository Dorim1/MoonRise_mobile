package com.example.moonrise

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "content")
data class Content(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val image: String,
    val title: String,
    val orTitle: String,
    val description: String,
    val category: String, // "фильм", "мультфильм", "аниме"
    val genres: String, // Храним как "Экшен, Фэнтези, Драма"
    val ageRating: String, // Например, "16+"
    val releaseDate: String // "2024-03-08"
)