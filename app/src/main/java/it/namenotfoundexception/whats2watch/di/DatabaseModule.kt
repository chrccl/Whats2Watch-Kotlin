package it.namenotfoundexception.whats2watch.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.namenotfoundexception.whats2watch.model.AppDatabase
import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.dao.PreferenceDao
import it.namenotfoundexception.whats2watch.model.dao.RoomDao
import it.namenotfoundexception.whats2watch.model.dao.UserDao
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, "whats2watch.db").build()
    }

    @Provides
    @Singleton
    fun userDao(db: AppDatabase): UserDao = db.userDao()

    @Provides
    @Singleton
    fun roomDao(db: AppDatabase): RoomDao = db.roomDao()

    @Provides
    @Singleton
    fun movieDao(db: AppDatabase): MovieDao = db.movieDao()

    @Provides
    @Singleton
    fun preferenceDao(db: AppDatabase): PreferenceDao = db.preferenceDao()
}