package it.namenotfoundexception.whats2watch.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.namenotfoundexception.whats2watch.model.entities.Review

@Dao
interface ReviewDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertReview(review: Review)

    @Query("SELECT * FROM reviews WHERE movie_id = :id ORDER BY rating DESC")
    suspend fun getMovieReviews(id: String): List<Review>

}