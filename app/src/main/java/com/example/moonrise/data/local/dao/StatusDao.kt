package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.Status
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStatus(status: Status)

    @Query("SELECT * FROM status WHERE contentId = :contentId LIMIT 1")
    fun getStatusByContentId(contentId: Int): Flow<Status?>

    @Query("DELETE FROM status WHERE contentId = :contentId")
    suspend fun removeStatus(contentId: Int)

    @Query("SELECT * FROM status WHERE contentId = :contentId LIMIT 1")
    suspend fun getStatusOnce(contentId: Int): Status?

    @Query("SELECT * FROM status WHERE contentId = :contentId LIMIT 1")
    fun getStatus(contentId: Int): Flow<Status?>

    @Query("SELECT * FROM status")
    fun getAllStatuses(): Flow<List<Status>>

}