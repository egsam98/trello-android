package com.project.trello_fintech.di.modules

import android.content.Context
import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.R
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.utils.StringsRepository
import com.project.trello_fintech.utils.TrelloUtil
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import javax.inject.Named


@Module
class TrelloApiModule {

    @Provides
    fun getRetrofitClient(cache: Cache, stringsRepository: StringsRepository) =
        RetrofitClient(cache, stringsRepository)

    @Provides
    @Named("authUrl")
    fun getAuthUrl(cxt: Context): String {
        val expiration = "30days"
        return "${BuildConfig.TRELLO_BASE_URL}authorize?" +
                "expiration=$expiration&" +
                "name=${cxt.resources.getString(R.string.app_name)}&" +
                "callback_method=fragment&" +
                "scope=read,write&" +
                "response_type=token&" +
                "key=${BuildConfig.TRELLO_API_KEY}&" +
                "return_url=${BuildConfig.TRELLO_URL_CALLBACK}"
    }

    @Provides
    fun getTrelloUtil(retrofitClient: RetrofitClient) = TrelloUtil(retrofitClient)
}