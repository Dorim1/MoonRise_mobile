package com.example.moonrise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "franchise_info")
data class FranchiseInfo(
    @PrimaryKey val franchiseId: Int,
    val description: String
)
