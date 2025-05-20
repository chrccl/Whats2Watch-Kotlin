package it.namenotfoundexception.whats2watch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.dao.PreferenceDao
import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.dao.RoomDao
import it.namenotfoundexception.whats2watch.model.dao.UserDao
import it.namenotfoundexception.whats2watch.model.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.model.repositories.PreferenceRepository
import it.namenotfoundexception.whats2watch.model.repositories.ReviewRepository
import it.namenotfoundexception.whats2watch.model.repositories.RoomRepository
import it.namenotfoundexception.whats2watch.model.repositories.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun userRepo(userDao: UserDao): UserRepository = UserRepository(userDao)

    @Provides
    @Singleton
    fun movieRepo(movieDao: MovieDao): MovieRepository = MovieRepository(movieDao)

    @Provides
    @Singleton
    fun preferenceRepo(preferenceDao: PreferenceDao): PreferenceRepository =
        PreferenceRepository(preferenceDao)

    @Provides
    @Singleton
    fun reviewRepo(reviewDao: ReviewDao): ReviewRepository = ReviewRepository(reviewDao)

    @Provides
    @Singleton
    fun roomRepo(roomDao: RoomDao): RoomRepository = RoomRepository(roomDao)
}