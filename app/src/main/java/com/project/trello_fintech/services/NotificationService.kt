package com.project.trello_fintech.services

import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.project.trello_fintech.Application
import javax.inject.Inject
import kotlin.reflect.KClass


/**
 * Сервис для работы с push-уведомлениями
 * @property cxt Context
 * @property manager NotificationManagerCompat
 */
class NotificationService @Inject constructor(): BroadcastReceiver() {
    companion object {
        private const val NOTIFICATION_ID_KEY = "notificationId"
        fun createIntent(cxt: Context, clazz: KClass<*>, notificationId: Int): Intent {
            return Intent(cxt, clazz.java).apply {
                putExtra(NOTIFICATION_ID_KEY, notificationId)
            }
        }
    }

    @Inject
    lateinit var cxt: Context

    private val manager by lazy {
        Application.component.inject(this)
        NotificationManagerCompat.from(cxt)
    }

    override fun onReceive(cxt: Context, intent: Intent) {
        cancel(intent)
    }

    fun notify(id: Int, notification: Notification) {
        manager.notify(id, notification)
    }

    fun cancel(intent: Intent) {
        val id = intent.getIntExtra(NOTIFICATION_ID_KEY, -1)
        cancel(id)
    }

    fun cancelAll() {
        manager.cancelAll()
    }

    fun cancel(id: Int) {
        manager.cancel(id)
    }
}