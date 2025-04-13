package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.data.local.entity.RelatedContent
import kotlinx.coroutines.flow.Flow

@Dao
interface RelatedContentDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(relatedContent: RelatedContent)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(list: List<RelatedContent>)

    @Query("SELECT * FROM related_content WHERE contentId = :contentId")
    fun getRelated(contentId: Int): List<RelatedContent>

    @Query("""
    SELECT * FROM content 
    WHERE id IN (
        SELECT relatedId FROM related_content WHERE contentId = :contentId
        UNION
        SELECT contentId FROM related_content WHERE relatedId = :contentId
    )
""")
    fun getRelatedContentWithCategory(contentId: Int): Flow<List<ContentWithCategory>>
}