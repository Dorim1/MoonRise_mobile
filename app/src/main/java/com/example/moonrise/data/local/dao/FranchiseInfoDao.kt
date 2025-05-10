package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.ContentWithCategory
import com.example.moonrise.data.local.entity.FranchiseInfo

@Dao
interface FranchiseInfoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(infoList: List<FranchiseInfo>)

    @Query("SELECT * FROM franchise_info WHERE franchiseId = :id")
    suspend fun getFranchiseInfo(id: Int): FranchiseInfo?


}