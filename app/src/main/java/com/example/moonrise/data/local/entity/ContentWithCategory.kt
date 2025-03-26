package com.example.moonrise.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ContentWithCategory (
    @Embedded val content: Content,
    @Relation (
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)