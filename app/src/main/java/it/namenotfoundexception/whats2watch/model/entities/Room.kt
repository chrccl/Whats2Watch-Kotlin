package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "rooms",
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["username_host"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["username_host"])
    ]
)
data class Room(
    @PrimaryKey
    @ColumnInfo(name = "code")
    val code: String,

    @ColumnInfo(name = "username_host")
    val usernameHost: String
)
