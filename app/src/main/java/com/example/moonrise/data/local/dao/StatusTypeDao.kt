package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.StatusType
import kotlinx.coroutines.flow.Flow

@Dao
interface StatusTypeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(statusTypes: List<StatusType>)

    @Query("SELECT * FROM status_type")
    suspend fun getAllStatusTypes(): List<StatusType>

    @Query("SELECT * FROM status_type WHERE id = :id")
    suspend fun getStatusTypeById(id: Int): StatusType?

    @Query("SELECT * FROM status_type")
    fun observeAllStatusTypes(): Flow<List<StatusType>>
}