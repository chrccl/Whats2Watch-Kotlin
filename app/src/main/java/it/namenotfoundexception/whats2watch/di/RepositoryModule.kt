package it.namenotfoundexception.whats2watch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.namenotfoundexception.whats2watch.api.TMDBService
import it.namenotfoundexception.whats2watch.model.dao.MovieDao
import it.namenotfoundexception.whats2watch.model.dao.PreferenceDao
import it.namenotfoundexception.whats2watch.model.dao.ReviewDao
import it.namenotfoundexception.whats2watch.model.dao.RoomDao
import it.namenotfoundexception.whats2watch.model.dao.UserDao
import it.namenotfoundexception.whats2watch.repositories.GenreRepository
import it.namenotfoundexception.whats2watch.repositories.MovieRepository
import it.namenotfoundexception.whats2watch.repositories.PreferenceRepository
import it.namenotfoundexception.whats2watch.repositories.ReviewRepository
import it.namenotfoundexception.whats2watch.repositories.RoomRepository
import it.namenotfoundexception.whats2watch.repositories.UserRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun userRepo(userDao: UserDao): UserRepository = UserRepository(userDao)

    @Provides
    @Singleton
    fun movieRepo(movieDao: MovieDao, api: TMDBService): MovieRepository =
        MovieRepository(movieDao, api)

    @Provides
    @Singleton
    fun preferenceRepo(preferenceDao: PreferenceDao): PreferenceRepository =
        PreferenceRepository(preferenceDao)

    @Provides
    @Singleton
    fun reviewRepo(reviewDao: ReviewDao, api: TMDBService): ReviewRepository =
        ReviewRepository(reviewDao, api)

    @Provides
    @Singleton
    fun roomRepo(roomDao: RoomDao): RoomRepository = RoomRepository(roomDao)

    @Provides
    @Singleton
    fun genreRepo(api: TMDBService): GenreRepository = GenreRepository(api)

}