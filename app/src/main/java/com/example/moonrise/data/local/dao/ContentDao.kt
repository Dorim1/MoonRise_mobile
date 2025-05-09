package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.data.local.entity.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContent(content: Content)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(content: List<Content>)

    @Query("SELECT * FROM content")
    fun getAllContent(): Flow<List<Content>>

    @Query("SELECT * FROM content WHERE id = :contentId")
    fun getContentById(contentId: Int): Flow<ContentWithCategory>

    @Query("SELECT COUNT(*) FROM content")
    suspend fun getContentCount(): Int

    @Query("SELECT * FROM content WHERE categoryId = :categoryId")
    fun getContentByCategory(categoryId: Int): Flow<List<Content>>

    @Query("SELECT * FROM content")
    fun getAllContentWithCategory(): Flow<List<ContentWithCategory>>

    @Query("""
    SELECT * FROM content 
    WHERE id = :contentId
""")
    fun getContentWithCategoryById(contentId: Int): Flow<ContentWithCategory>

    @Query("""
    SELECT genre.* FROM genre
    INNER JOIN content_genre ON genre.id = content_genre.genreId
    WHERE content_genre.contentId = :contentId
""")
    fun getGenresForContent(contentId: Int): Flow<List<Genre>>
}