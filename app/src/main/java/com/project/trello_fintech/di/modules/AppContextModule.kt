package com.project.trello_fintech.di.modules

import android.content.Context
import com.project.trello_fintech.utils.StringsRepository
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import javax.inject.Singleton


@Module
class AppContextModule(private val appContext: Context) {

    @Provides
    @Singleton
    fun getContext() = appContext

    @Provides
    @Singleton
    fun getCache() = Cache(appContext.cacheDir, 4096)

    @Provides
    @Singleton
    fun getPreferencesRepository() = StringsRepository(appContext)
}