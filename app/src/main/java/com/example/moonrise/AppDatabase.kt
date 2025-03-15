package com.example.moonrise

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

@Database(entities = [Content::class, Status::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun contentDao(): ContentDao
    abstract fun statusDao(): StatusDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "moonrise_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }

        private fun loadJsonData(context: Context): List<Content> {
            val jsonString = context.assets.open("data.json").bufferedReader().use { it.readText() }
            val gson = Gson()
            return gson.fromJson(jsonString, object : TypeToken<List<Content>>() {}.type)
        }

    }
}