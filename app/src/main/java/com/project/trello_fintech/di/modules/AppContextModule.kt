package com.project.trello_fintech.di.modules

import android.content.Context
import com.google.gson.GsonBuilder
import com.project.trello_fintech.utils.StringsRepository
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import retrofit2.converter.gson.GsonConverterFactory


@Module
class AppContextModule(private val appContext: Context) {

    @Provides
    fun getContext() = appContext

    @Provides
    fun getCache() = Cache(appContext.cacheDir, 4096)

    @Provides
    fun getPreferencesRepository() = StringsRepository(appContext)

    @Provides
    fun gsonConverterFactory(): GsonConverterFactory {
        val gson = GsonBuilder().setLenient().create()
        return GsonConverterFactory.create(gson)
    }
}