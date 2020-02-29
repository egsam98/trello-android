package com.project.trello_fintech

import android.app.Application
import com.project.trello_fintech.di.components.AppComponent
import com.project.trello_fintech.di.components.DaggerAppComponent
import com.project.trello_fintech.di.modules.AppContextModule


class Application: Application() {
    companion object {
        lateinit var component: AppComponent
            private set
    }

    override fun onCreate() {
        super.onCreate()
        component = DaggerAppComponent.builder()
            .appContextModule(AppContextModule(applicationContext))
            .build()
    }
}