package it.namenotfoundexception.whats2watch.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.namenotfoundexception.whats2watch.model.entities.Movie

@Dao
interface MovieDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertMovie(movie: Movie)

    @Query("SELECT * FROM movies WHERE movie_id = :id")
    suspend fun getMovieById(id: String): Movie

    @Query("SELECT * FROM movies WHERE movie_id IN(:ids)")
    suspend fun getMoviesByIds(ids: List<String>): List<Movie>

}