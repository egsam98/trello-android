package com.project.trello_fintech

import android.app.Application
import com.google.firebase.messaging.FirebaseMessaging
import com.project.trello_fintech.di.components.AppComponent
import com.project.trello_fintech.di.components.DaggerAppComponent
import com.project.trello_fintech.di.modules.AppContextModule
import com.project.trello_fintech.models.firebase.FirebaseMessage


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

        FirebaseMessaging.getInstance().subscribeToTopic(FirebaseMessage.TOPIC_NAME)
    }
}