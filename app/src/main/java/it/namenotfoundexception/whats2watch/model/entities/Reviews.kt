package it.namenotfoundexception.whats2watch.model.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey

@Entity(
    tableName = "reviews",
    primaryKeys = ["user", "movie_id"],
    foreignKeys = [
        ForeignKey(
            entity = User::class,
            parentColumns = ["username"],
            childColumns = ["user"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Reviews(
    @ColumnInfo(name = "user")
    val user: String,

    @ColumnInfo(name = "movie_id")
    val movieId: String,

    @ColumnInfo(name = "rating")
    val rating: Float,              // es. da 0.0 a 5.0

    @ColumnInfo(name = "comment")
    val comment: String
)
