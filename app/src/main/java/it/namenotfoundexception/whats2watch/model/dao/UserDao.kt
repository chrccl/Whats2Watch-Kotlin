package it.namenotfoundexception.whats2watch.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import it.namenotfoundexception.whats2watch.model.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM users WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("DELETE FROM users WHERE username = :username")
    suspend fun deleteUserByUsername(username: String)

}