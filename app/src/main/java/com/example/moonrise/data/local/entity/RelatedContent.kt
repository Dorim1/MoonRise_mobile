package com.example.moonrise.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "related_content",
    primaryKeys = ["contentId", "relatedId"],
    foreignKeys = [
        ForeignKey(entity = Content::class, parentColumns = ["id"], childColumns = ["contentId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = Content::class, parentColumns = ["id"], childColumns = ["relatedId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("relatedId")]
)
data class RelatedContent(
    val contentId: Int,
    val relatedId: Int
)
