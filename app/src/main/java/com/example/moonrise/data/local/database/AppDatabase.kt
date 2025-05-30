package com.example.moonrise.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.moonrise.data.local.dao.CategoryDao
import com.example.moonrise.data.local.dao.ContentDao
import com.example.moonrise.data.local.dao.ContentGenreDao
import com.example.moonrise.data.local.dao.FranchiseInfoDao
import com.example.moonrise.data.local.dao.GenreDao
import com.example.moonrise.data.local.dao.RatingDao
import com.example.moonrise.data.local.dao.StatusDao
import com.example.moonrise.data.local.dao.StatusTypeDao
import com.example.moonrise.data.local.entity.Category
import com.example.moonrise.data.local.entity.Content
import com.example.moonrise.data.local.entity.ContentGenre
import com.example.moonrise.data.local.entity.FranchiseInfo
import com.example.moonrise.data.local.entity.Genre
import com.example.moonrise.data.local.entity.Rating
import com.example.moonrise.data.local.entity.Status
import com.example.moonrise.data.local.entity.StatusType


@Database(entities = [Content::class, Status::class, StatusType::class, Category::class, ContentGenre::class, Genre::class, Rating::class, FranchiseInfo::class], version = 64, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun statusDao(): StatusDao
    abstract fun statusTypeDao(): StatusTypeDao
    abstract fun categoryDao(): CategoryDao
    abstract fun genreDao(): GenreDao
    abstract fun contentGenreDao(): ContentGenreDao
    abstract fun ratingDao(): RatingDao
    abstract fun franchiseInfoDao(): FranchiseInfoDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moonrise_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}