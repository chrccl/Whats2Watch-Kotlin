package it.namenotfoundexception.whats2watch.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import it.namenotfoundexception.whats2watch.BuildConfig
import it.namenotfoundexception.whats2watch.api.TMDBService
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://api.themoviedb.org/3/"

    @Provides @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val apiKey = BuildConfig.TMDB_API_KEY
        val interceptor = Interceptor { chain ->
            val original = chain.request()
            val url: HttpUrl = original.url
                .newBuilder()
                .addQueryParameter("api_key", apiKey)
                .build()
            val request: Request = original.newBuilder().url(url).build()
            chain.proceed(request)
        }
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit =
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides @Singleton
    fun provideTmdbApiService(retrofit: Retrofit): TMDBService =
        retrofit.create(TMDBService::class.java)
}