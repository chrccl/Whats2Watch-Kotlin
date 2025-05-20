package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoomWithUsers(
    @Embedded val room: Room,

    @Relation(
        parentColumn = "code", // Room.code
        entityColumn = "username", // User.username
        associateBy = Junction(
            value = RoomParticipant::class,
            parentColumn = "room_code", // RoomParticipant.roomCode
            entityColumn = "member"     // RoomParticipant.username
        )
    )
    val participants: List<User>
)
