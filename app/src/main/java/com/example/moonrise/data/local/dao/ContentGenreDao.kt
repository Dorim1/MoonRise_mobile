package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.ContentGenre
import com.example.moonrise.data.local.entity.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface ContentGenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(contentGenre: ContentGenre)

    @Query("""
        SELECT genre.* FROM genre 
        INNER JOIN content_genre ON genre.id = content_genre.genreId 
        WHERE content_genre.contentId = :contentId
    """)
    fun getGenresForContent(contentId: Int): Flow<List<Genre>>

    @Query("""
        SELECT content.* FROM content 
        INNER JOIN content_genre ON content.id = content_genre.contentId 
        WHERE content_genre.genreId = :genreId
    """)
    fun getContentByGenre(genreId: Int): Flow<List<Content>>
}