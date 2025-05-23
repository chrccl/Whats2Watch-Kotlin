package it.namenotfoundexception.whats2watch.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Preference

@Dao
interface PreferenceDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertPreference(preference: Preference)

    @Query("SELECT * FROM preferences WHERE room_code = :code")
    suspend fun getPreferencesByRoom(code: String): List<Preference>

    @Query("SELECT * FROM preferences WHERE room_code = :code AND participant_name = :username")
    suspend fun getPreferencesByUser(code: String, username: String): List<Preference>

    @Query(
        """
        SELECT * FROM movies WHERE movie_id IN(
            SELECT p.movie_id
            FROM preferences AS p
            WHERE p.room_code = :roomCode
              AND p.liked = 1
            GROUP BY p.movie_id
            HAVING COUNT(p.movie_id) = (
                SELECT COUNT(*) 
                FROM room_participants 
                WHERE room_code = :roomCode
            )
        )
    """
    )
    suspend fun getRoomMatches(roomCode: String): List<Movie>
}