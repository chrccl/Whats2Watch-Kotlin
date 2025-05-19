package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey()
    val username: String,

    val password: String
)
