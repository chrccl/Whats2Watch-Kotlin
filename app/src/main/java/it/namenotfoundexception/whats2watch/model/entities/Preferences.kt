package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "preferences",
    primaryKeys = ["room_code", "participant_name", "movie_id"],
    foreignKeys = [
        ForeignKey(
            entity = RoomParticipant::class,
            parentColumns = ["room_code", "member"],
            childColumns = ["room_code", "participant_name"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Preferences(
    @ColumnInfo(name = "room_code")
    val roomCode: String,

    @ColumnInfo(name = "participant_name")
    val participantName: String,

    @ColumnInfo(name = "movie_id")
    val movieId: String,

    @ColumnInfo(name = "liked")
    val liked: Boolean              // true = like, false = dislike
)

