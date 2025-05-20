package it.namenotfoundexception.whats2watch.model

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.dao.PreferenceDao
import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.dao.RoomDao
import it.namenotfoundexception.whats2watch.model.dao.UserDao
import it.namenotfoundexception.whats2watch.model.entities.Movie
import it.namenotfoundexception.whats2watch.model.entities.Preference
import it.namenotfoundexception.whats2watch.model.entities.Reviews
import it.namenotfoundexception.whats2watch.model.entities.RoomParticipant
import it.namenotfoundexception.whats2watch.model.entities.User

@Database(
    entities = [
        Room::class,
        RoomParticipant::class,
        User::class,
        Movie::class,
        Preference::class,
        Reviews::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun roomDao(): RoomDao
    abstract fun movieDao(): MovieDao
    abstract fun preferenceDao(): PreferenceDao
    abstract fun reviewDao(): ReviewDao
    abstract fun userDao(): UserDao

}