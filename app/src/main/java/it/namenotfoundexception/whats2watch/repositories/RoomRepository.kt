package it.namenotfoundexception.whats2watch.repositories

import it.namenotfoundexception.whats2watch.model.dao.RoomDao
import it.namenotfoundexception.whats2watch.model.entities.Room
import it.namenotfoundexception.whats2watch.model.entities.RoomParticipant
import it.namenotfoundexception.whats2watch.model.entities.RoomWithUsers
import javax.inject.Inject

class RoomRepository @Inject constructor(
    private val roomDao: RoomDao
) {

    suspend fun saveRoom(room: Room) {
        roomDao.insertRoom(room)
    }

    suspend fun insertParticipant(participant: RoomParticipant) {
        roomDao.insertParticipant(participant)
    }

    suspend fun getRoomWithUsers(code: String): RoomWithUsers {
        return roomDao.getRoomWithUsers(code)
    }

    suspend fun removeParticipant(code: String, username: String) {
        roomDao.removeParticipant(code, username)
    }

    suspend fun getRoomsByUser(username: String): List<Room>? {
        return roomDao.getRoomsByUser(username)
    }

    suspend fun deleteRoom(code: String) {
        roomDao.deleteRoom(code)
    }

    suspend fun removeAllParticipants(code: String) {
        roomDao.removeAllParticipants(code)
    }

    suspend fun getRoomByCode(code: String): Room {
        return roomDao.getRoomByCode(code)
    }

}