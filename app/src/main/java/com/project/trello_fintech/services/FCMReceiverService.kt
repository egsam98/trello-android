package com.project.trello_fintech.services

import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


/**
 * Firebase Cloud Messaging Receiver
 */
class FCMReceiverService : FirebaseMessagingService() {

    override fun onMessageReceived(msg: RemoteMessage) {
        msg.notification?.let { sendNotification(it) }
    }

    private fun sendNotification(notificationMsg: RemoteMessage.Notification) {
        val notification = NotificationCompat.Builder(applicationContext, "")
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentTitle(notificationMsg.title)
            .setContentText(notificationMsg.body)
            .build()

        NotificationManagerCompat.from(applicationContext).notify(0, notification)
    }
}