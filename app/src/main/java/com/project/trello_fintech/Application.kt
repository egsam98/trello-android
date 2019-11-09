package com.project.trello_fintech

import android.app.Application
import android.preference.PreferenceManager
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.utils.StringsRepository
import okhttp3.Cache


class Application: Application() {
    override fun onCreate() {
        super.onCreate()

        RetrofitClient.cache = Cache(cacheDir, 4096)
        StringsRepository.attach(PreferenceManager.getDefaultSharedPreferences(this))
    }
}