package com.project.trello_fintech.di.modules

import android.content.Context
import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.di.scopes.MainActivityScope
import dagger.Module
import dagger.Provides
import javax.inject.Named


@Module
class MainActivityModule(private val mainActivity: MainActivity) {

    @Provides
    @MainActivityScope
    fun getMainActivity() = mainActivity

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
}