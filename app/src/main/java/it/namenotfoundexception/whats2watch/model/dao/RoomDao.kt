package it.namenotfoundexception.whats2watch.model.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.Companion.REPLACE
import androidx.room.Query
import androidx.room.Transaction
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.model.entities.RoomParticipant
import it.namenotfoundexception.whats2watch.model.entities.RoomWithUsers

@Dao
interface RoomDao {

    @Insert(onConflict = REPLACE)
    suspend fun insertRoom(room: Room)

    @Insert(onConflict = REPLACE)
    suspend fun insertParticipant(part: RoomParticipant)

    @Insert(onConflict = REPLACE)
    suspend fun insertParticipants(parts: List<RoomParticipant>)

    @Query("SELECT * FROM rooms WHERE code = :code")
    suspend fun getRoomByCode(code: String): Room

    @Transaction
    @Query("SELECT * FROM rooms WHERE code = :code")
    suspend fun getRoomWithUsers(code: String): RoomWithUsers

    @Query("DELETE FROM room_participants WHERE room_code = :code AND member = :username")
    suspend fun removeParticipant(code: String, username: String)

    @Query("DELETE FROM room_participants WHERE room_code = :code")
    suspend fun removeParticipants(code: String)

    @Query("SELECT * FROM rooms WHERE username_host = :username")
    suspend fun getRoomsByUser(username: String) : List<Room>

}