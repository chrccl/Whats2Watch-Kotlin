package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "room_participants",
    primaryKeys = ["room_code", "member"],
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
    ],
    indices = [
        Index(value = ["member"]),
        Index(value = ["room_code"])
    ]
)
data class RoomParticipant(
    @ColumnInfo(name = "room_code")
    val roomCode: String,

    @ColumnInfo(name = "member")
    val username: String
)
