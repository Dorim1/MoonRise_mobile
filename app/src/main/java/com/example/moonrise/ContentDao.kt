package com.example.moonrise

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: Content)

    @Query("SELECT * FROM content")
    fun getAllContent(): Flow<List<Content>>

    @Query("SELECT * FROM content WHERE id = :contentId")
    fun getContentById(contentId: Int): Flow<Content>
}