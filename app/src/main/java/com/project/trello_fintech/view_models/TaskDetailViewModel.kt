package com.project.trello_fintech.view_models

import android.app.Notification
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveEvent
import okhttp3.RequestBody
import android.net.Uri
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskAttachmentApi
import com.project.trello_fintech.utils.reactive.LiveList
import okhttp3.MediaType
import okhttp3.MultipartBody
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.project.trello_fintech.activities.MainActivity
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalArgumentException
import com.project.trello_fintech.R


/**
 * ViewModel для манипуляций над выбранной задачей
 * @property cxt Context
 * @property retrofitClient RetrofitClient
 * @property taskId String
 * @property taskText String
 * @property attachments LiveList<Attachment>
 * @property attachmentRetrofit TaskAttachmentApi
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property onAttachmentClick LiveEvent<Attachment>
 * @property actionsTitle Array<String>
 * @property actions Array<LiveEvent<Unit>?>
 */
class TaskDetailViewModel(private val cxt: Context, private val retrofitClient: RetrofitClient): CleanableViewModel() {

    private var taskId = ""
    var taskText = ""
        private set
    private val attachments = LiveList<Task.Attachment>()

    private val attachmentRetrofit by lazy {
        retrofitClient.create<TaskAttachmentApi>(onError)
    }

    val onError = LiveEvent<Pair<String, Int?>>()
    val onAttachmentClick = LiveEvent<Task.Attachment>()

    val actionsTitle = arrayOf("Загрузить вложение", "Array item one", "Array item two")
    val actions = arrayOf(LiveEvent<Unit>(), null, null)

    fun attachTask(task: Task) {
        taskId = task.id
        taskText = task.text
        attachments.data = task.attachments.toMutableList()
    }

    fun observeAttachments(subscribe: (MutableList<Task.Attachment>) -> Unit) {
        val disposable = attachments.observe().subscribe(subscribe)
        clearOnDestroy(disposable)
    }

    fun uploadAttachment(fileUri: Uri) {
        val mimeType = cxt.contentResolver.getType(fileUri)
        if (mimeType == null) {
            onError.emit(Pair("Неизвестный MIME-тип", null))
            return
        }

        val fileInputStream: InputStream?
        try {
            fileInputStream = cxt.contentResolver.openInputStream(fileUri)
        } catch (e: IOException) {
            onError.emit(Pair(e.message?: e.toString(), null))
            return
        }

        fileInputStream?.use {
            val part: MultipartBody.Part
            try {
                part = createMultipartBody(fileUri, fileInputStream)
            } catch (e: IllegalArgumentException) {
                onError.emit(Pair(e.message?: e.toString(), null))
                return
            }
            attachmentRetrofit.create(taskId, mimeType, part)
                .doOnSubscribe {
                    Toast.makeText(cxt, "Загрузка файла началась, не закрывайте приложение", Toast.LENGTH_LONG).show()
                }
                .subscribe { attachment ->
                    sendReadyNotification(attachment)
                    attachments add attachment
                }
        }
    }

    private fun createMultipartBody(fileUri: Uri, fileInputStream: InputStream): MultipartBody.Part {
        var filename = ""
        cxt.contentResolver.query(fileUri, arrayOf(MediaStore.MediaColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            if (cursor.moveToFirst()) {
                filename = cursor.getString(0)
            }
        }
        val requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), fileInputStream.readBytes())
        return MultipartBody.Part.createFormData("file", filename, requestFile)
    }

    private fun sendReadyNotification(attachment: Task.Attachment) {
        val returnToAppIntent = PendingIntent.getActivity(cxt, 0,
            Intent(cxt, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(cxt, "")
            .setContentTitle(cxt.resources.getString(R.string.app_name))
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentText("Результат загрузки вложения")
            .setDefaults(Notification.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Вложение ${attachment.name} успешно загружено в задачу $taskText\n" +
                        "Зайдите в приложение, чтобы проверить"))
            .setContentIntent(returnToAppIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(cxt).notify(0, notification)
    }

    fun removeAttachment(attachment: Task.Attachment) {
        attachmentRetrofit.delete(taskId, attachment.id).subscribe()
        attachments.remove(attachment)
    }
}