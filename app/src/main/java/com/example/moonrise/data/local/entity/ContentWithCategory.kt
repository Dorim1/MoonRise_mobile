package com.example.moonrise.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class ContentWithCategory(
    @Embedded val content: Content,

    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category,

    @Relation(
        parentColumn = "id",
        entityColumn = "contentId"
    )
    val status: Status?,

    @Relation(
        parentColumn = "id",
        entityColumn = "contentId"
    )
    val rating: Rating?
)