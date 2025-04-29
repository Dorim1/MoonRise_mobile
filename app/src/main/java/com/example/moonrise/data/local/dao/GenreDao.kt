package com.example.moonrise.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.moonrise.data.local.entity.Genre
import kotlinx.coroutines.flow.Flow

@Dao
interface GenreDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertGenreList(genres: List<Genre>)

    @Query("SELECT * FROM genre")
    fun getAllGenres(): Flow<List<Genre>>

    @Query("SELECT * FROM genre WHERE id = :genreId")
    fun getGenreById(genreId: Int): Flow<Genre>

}