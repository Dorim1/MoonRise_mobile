package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.Rating
import kotlinx.coroutines.flow.Flow

@Dao
interface RatingDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rating: Rating)

    @Query("SELECT * FROM ratings WHERE contentId = :contentId LIMIT 1")
    fun getRatingForContent(contentId: Int): Flow<Rating?>

    @Query("SELECT AVG(ratingValue) FROM ratings WHERE contentId = :contentId")
    fun getAverageRating(contentId: Int): Flow<Float?>

    @Query("DELETE FROM ratings WHERE contentId = :contentId")
    suspend fun deleteByContentId(contentId: Int)
}