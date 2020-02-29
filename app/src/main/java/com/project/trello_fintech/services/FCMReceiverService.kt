package com.project.trello_fintech.services

import android.app.PendingIntent
import android.media.RingtoneManager
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.project.trello_fintech.Application
import com.project.trello_fintech.activities.VideoCallActivity
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.models.firebase.FirebaseMessage
import com.project.trello_fintech.services.utils.NotificationType
import javax.inject.Inject
import com.project.trello_fintech.R
import com.project.trello_fintech.utils.getBitmap


/**
 * Firebase Cloud Messaging Receiver
 * @property retrofitClient RetrofitClient
 * @property notificationService NotificationService
 * @property authService AuthenticationService
 */
class FCMReceiverService: FirebaseMessagingService() {

    @Inject lateinit var retrofitClient: RetrofitClient
    @Inject lateinit var notificationService: NotificationService
    @Inject lateinit var authService: AuthenticationService

    override fun onCreate() {
        super.onCreate()
        Application.component.inject(this)
    }

    override fun onMessageReceived(msg: RemoteMessage) {
        val data = FirebaseMessage.parseData(msg)
        data?.let { sendNotification(msg, it) }
    }

    private fun sendNotification(msg: RemoteMessage, data: FirebaseMessage.Data) {
        val notificationId = msg.from?.hashCode()?: -1

        val alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE)
        val vibration = LongArray(10) { 1000 }

        val notification = NotificationCompat.Builder(this, "")
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setLargeIcon(resources.getBitmap(R.drawable.trello_icon))
            .setContentTitle(data.title)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(data.body))
            .setContentText(data.body)
            .setVibrate(vibration)
            .setSound(alarmSound)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .apply {
                if (data.notificationType == NotificationType.ACCEPTDECLINE) {
                    addAction(android.R.drawable.ic_delete, resources.getString(R.string.decline),
                        createCancelIntent(notificationId))
                    addAction(android.R.drawable.stat_notify_chat, resources.getString(R.string.accept),
                        createVideoCallIntent(data, notificationId))
                }
            }
            .build()

        notificationService.notify(notificationId, notification)
    }

    private fun createCancelIntent(notificationId: Int): PendingIntent {
        val intent = NotificationService.createIntent(this, NotificationService::class, notificationId)
        return PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }

    private fun createVideoCallIntent(data: FirebaseMessage.Data, notificationId: Int): PendingIntent {
        val intent = VideoCallActivity.createNotificationIntent(this, data.boardId, notificationId)
        return PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}