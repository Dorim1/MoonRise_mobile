package com.example.moonrise.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "status_type")
data class StatusType(
    @PrimaryKey val id: Int,
    val name: String
)