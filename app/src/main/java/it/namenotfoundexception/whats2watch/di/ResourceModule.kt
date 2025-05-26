package it.namenotfoundexception.whats2watch.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import it.namenotfoundexception.whats2watch.model.ResourceProvider
import javax.inject.Inject

class AndroidResourceProvider @Inject constructor(
    @ApplicationContext private val ctx: Context
) : ResourceProvider {
    override fun getString(id: Int, vararg args: Any?) =
        ctx.getString(id, *args)
}

@Module @InstallIn(SingletonComponent::class)
object ResourceModule {
    @Provides fun provideResourceProvider(
        impl: AndroidResourceProvider
    ): ResourceProvider = impl
}