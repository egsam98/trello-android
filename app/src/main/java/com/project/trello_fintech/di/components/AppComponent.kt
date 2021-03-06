package com.project.trello_fintech.di.components

import android.content.Context
import com.project.trello_fintech.activities.GanttChartActivity
import com.project.trello_fintech.activities.VideoCallActivity
import com.project.trello_fintech.di.modules.*
import com.project.trello_fintech.services.FCMReceiverService
import com.project.trello_fintech.services.NotificationService
import com.project.trello_fintech.services.VideoCallWatcherService
import dagger.Component
import javax.inject.Singleton

/**
 * Компонент, предоставляющий долгоживущие классы (ЖЦ согласно ApplicationContext)
 */
@Component(modules = [AppContextModule::class])
@Singleton
interface AppComponent {
    val context: Context
    fun plusMainActivityComponent(mainActivityModule: MainActivityModule): MainActivityComponent

    fun inject(videoCallActivity: VideoCallActivity)
    fun inject(ganttChartActivity: GanttChartActivity)

    fun inject(fcmReceiverService: FCMReceiverService)
    fun inject(notificationService: NotificationService)
    fun inject(videoCallWatcherService: VideoCallWatcherService)
}