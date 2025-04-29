package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
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

    @Query("SELECT MIN(CAST(SUBSTR(releaseDate, 1, LENGTH(releaseDate) - 1) AS INTEGER)) FROM content")
    suspend fun getMinYear(): Int?

    @Query("SELECT MAX(CAST(SUBSTR(releaseDate, 1, LENGTH(releaseDate) - 1) AS INTEGER)) FROM content")
    suspend fun getMaxYear(): Int?

    suspend fun getYearRange(): Pair<Int?, Int?> {
        val minYear = getMinYear()
        val maxYear = getMaxYear()
        return Pair(minYear, maxYear)
    }

    @Query("SELECT DISTINCT ageRating FROM content WHERE ageRating IS NOT NULL")
    fun getAllAgeRatings(): Flow<List<String>>

    @Transaction
    @Query("""
    SELECT * FROM content
    WHERE (:category IS NULL OR categoryId IN (
        SELECT id FROM category WHERE name = :category
    ))
    AND (:statusId IS NULL OR content.id IN (
        SELECT contentId FROM status WHERE statusTypeId = :statusId
    ))
    AND (:ageRating IS NULL OR ageRating = :ageRating)
    AND (
        :genresSize = 0 OR id IN (
            SELECT contentId FROM content_genre
            INNER JOIN genre ON content_genre.genreId = genre.id
            WHERE genre.name IN (:genres)
        )
    )
""")
    fun getFilteredContentWithRelations(
        genres: List<String>,
        genresSize: Int,
        category: String?,
        statusId: Int?,
        ageRating: String?
    ): Flow<List<ContentWithCategory>>
}