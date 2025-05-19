package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class RoomWithUsers(
    @Embedded val room: Room,

    @Relation(
        parentColumn = "code",
        entityColumn = "room_code",
        associateBy = Junction(RoomParticipant::class)
    )
    val participants: List<User>
)
