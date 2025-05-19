package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "room_participants",
    primaryKeys = ["room_code", "username"],
    foreignKeys = [
        ForeignKey(
            entity = Room::class,
            parentColumns = ["code"],
            childColumns = ["room_code"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["member"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomParticipant(
    @ColumnInfo(name = "room_code")
    val roomCode: String,

    @ColumnInfo(name = "member")
    val username: String
)
