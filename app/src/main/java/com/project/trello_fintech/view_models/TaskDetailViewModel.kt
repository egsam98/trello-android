package com.project.trello_fintech.view_models

import android.app.Notification
import android.content.Context
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveEvent
import okhttp3.RequestBody
import android.net.Uri
import com.project.trello_fintech.utils.reactive.LiveList
import okhttp3.MediaType
import okhttp3.MultipartBody
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.MutableLiveData
import java.io.IOException
import java.io.InputStream
import java.lang.IllegalArgumentException
import com.project.trello_fintech.R
import com.project.trello_fintech.api.*
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.models.User
import com.project.trello_fintech.services.FirebaseService
import com.project.trello_fintech.utils.*
import io.reactivex.Observable
import io.reactivex.functions.Function3


/**
 * Доп. данные, получаемые из Firebase Firestore (не поддерживаются Trello API)
 * @property vcsUrl MutableLiveData<String>
 */
class FirebaseData {
    val vcsUrl = MutableLiveData<String>()
}

/**
 * ViewModel для манипуляций над выбранной задачей
 * @property cxt Context
 * @property retrofitClient RetrofitClient
 * @property firebaseService FirebaseService
 * @property task MutableLiveData<Task>
 * @property attachments LiveList<Attachment>
 * @property attachmentRetrofit TaskAttachmentApi
 * @property taskRetrofit TaskApi
 * @property historyRetrofit TaskHistoryApi
 * @property userRetrofit UserApi
 * @property checklistRetrofit ChecklistApi
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property onAttachmentClick LiveEvent<Attachment>
 * @property onOpenHistory LiveEvent<Unit>
 * @property actionsTitle Array<String>
 * @property trelloUtil TrelloUtil
 * @property historyList MutableLiveData<List<History>>
 * @property isLoading MutableLiveData<Boolean>
 * @property participants MutableLiveData<List<User>>
 * @property checklists MutableLiveData<MutableList<Checklist>>
 * @property firebaseData FirebaseData
 */
class TaskDetailViewModel(
    private val cxt: Context,
    private val retrofitClient: RetrofitClient,
    private val trelloUtil: TrelloUtil,
    private val firebaseService: FirebaseService): CleanableViewModel() {

    var task = MutableLiveData<Task>(Task())
    private val attachments = LiveList<Task.Attachment>()

    private val attachmentRetrofit by lazy { retrofitClient.create<TaskAttachmentApi>(onError) }
    private val taskRetrofit by lazy { retrofitClient.create<TaskApi>(onError) }
    private val historyRetrofit by lazy { retrofitClient.create<TaskHistoryApi>(onError) }
    private val userRetrofit by lazy { retrofitClient.create<UserApi>(onError) }
    private val checklistRetrofit by lazy { retrofitClient.create<ChecklistApi>(onError) }

    val onError = LiveEvent<Pair<String, Int?>>()
    val onAttachmentClick = LiveEvent<Task.Attachment>()
    val onOpenHistory = LiveEvent<Unit>()

    val historyList = MutableLiveData<List<Task.History>>()
    val isLoading = MutableLiveData<Boolean>(false)
    val participants = MutableLiveData<List<User>>()
    val checklists = MutableLiveData<MutableList<Checklist>>()

    val firebaseData = FirebaseData()

    private fun loadFromFirebase(task: Task) {
        firebaseService.getTask(task) { document ->
            firebaseData.vcsUrl.value = document.getString("vcsUrl").orEmpty()
        }
    }

    fun attachTask(task: Task, boardBackgroundView: View) {
        val s1 = attachmentRetrofit.findAllByTaskId(task.id).toObservable()
        val s2 = userRetrofit.findAllByTaskId(task.id).toObservable()
        val s3 = checklistRetrofit.findAllByTaskId(task.id).toObservable()
        val disposable = Observable.zip<List<Task.Attachment>, List<User>, List<Checklist>, Unit> (s1, s2, s3,
            Function3 { attachments, participants, checklists ->
                this.attachments.data = attachments.toMutableList()
                this.participants.value = participants
                this.checklists.value = checklists.toMutableList()
            })
            .doOnSubscribe { isLoading.value = true }
            .doOnComplete { isLoading.value = false }
            .subscribe { this.task.value = task }
        loadFromFirebase(task)
        loadBoardBackground(task, boardBackgroundView)
        clearOnDestroy(disposable)
    }

    fun attachTask(taskId: String, boardBackgroundView: View) {
        val disposable = taskRetrofit.findById(taskId).subscribe {
                task -> attachTask(task, boardBackgroundView)
        }
        clearOnDestroy(disposable)
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

            val observable = attachmentRetrofit.create(task.value!!.id, mimeType, part)
                .toObservable()
                .doOnSubscribe {
                    Toast.makeText(cxt, "Загрузка файла началась, не закрывайте приложение", Toast.LENGTH_LONG).show()
                }
                .share()
            observable.subscribe { sendReadyNotification(it) }
            val disposable = observable.subscribe { attachment ->
                attachments add attachment
            }
            clearOnDestroy(disposable)
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
        val notification = NotificationCompat.Builder(cxt, "")
            .setContentTitle(cxt.resources.getString(R.string.app_name))
            .setSmallIcon(android.R.drawable.sym_def_app_icon)
            .setContentText("Результат загрузки вложения")
            .setDefaults(Notification.DEFAULT_ALL)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText("Вложение ${attachment.name} успешно загружено в задачу ${task.value!!.text}"))
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(cxt).notify(0, notification)
    }

    fun removeAttachment(attachment: Task.Attachment) {
        attachmentRetrofit.delete(task.value!!.id, attachment.id).subscribe()
        attachments remove attachment
    }

    fun updateInputs() {
        val task = task.value!!
        taskRetrofit.updateDescription(task.id, task.description).subscribe()
        firebaseService.getTask(task) {
            it.reference.setField("vcsUrl", firebaseData.vcsUrl.value.orEmpty())
        }
    }

    fun showHistory() {
        val disposable = historyRetrofit.findAllByTaskId(task.value!!.id)
            .doOnSubscribe { isLoading.value = true }
            .doOnSuccess { isLoading.value = false }
            .subscribe { historyList ->
                this.historyList.value = historyList
                onOpenHistory.emit()
            }
        clearOnDestroy(disposable)
    }

    fun createChecklist(title: String) {
        val disposable = checklistRetrofit.create(title, task.value!!.id).subscribe { checklist ->
            checklists add checklist
        }
        clearOnDestroy(disposable)
    }

    fun updateChecklistTitle(id: String, newTitle: String) {
        val disposable = checklistRetrofit.updateTitle(id, newTitle)
            .doOnSubscribe { isLoading.value = true }
            .doOnComplete { isLoading.value = false }
            .subscribe {
                checklists.value?.find { it.id == id }?.let {
                    it.title = newTitle
                    checklists.update()
                }
            }
        clearOnDestroy(disposable)
    }

    fun deleteChecklist(id: String) {
        val disposable = checklistRetrofit.delete(id).subscribe {
            checklists.value!!.find { it.id == id }
                ?.let { checklists remove it }
        }
        clearOnDestroy(disposable)
    }

    fun createCheckitem(checklistId: String, text: String) {
        val disposable = checklistRetrofit.createItem(checklistId, text).subscribe { checkitem ->
            val checklist = checklists.value!!.find { it.id == checklistId }
            checklist?.items?.add(checkitem)
            checklists.update()
        }
        clearOnDestroy(disposable)
    }

    fun updateCheckitem(checkitemId: String, title: String? = null, isChecked: Boolean? = null) {
        val params = mutableMapOf<String, String>()
        title?.let { params["name"] = it }
        isChecked?.let { params["state"] = Checklist.Item.stateOf(it) }
        checklistRetrofit.updateItem(task.value!!.id, checkitemId, params).subscribe()

        val checkitem = checklists.value!!.flatMap { it.items }.find { it.id == checkitemId }
        title?.let { checkitem?.title = it }
        isChecked?.let { checkitem?.setState(it) }

        checklists.update()
    }

    fun deleteCheckitem(checklistId: String, checkitemId: String) {
        checklistRetrofit.deleteItem(checklistId, checkitemId).subscribe()
        checklists.value!!.find { it.id == checklistId }?.let {
            val deletedCheckitem = it.items.find { checkitem -> checkitem.id == checkitemId }
            it.items.remove(deletedCheckitem)
            checklists.update()
        }
    }

    fun findAllTasksInBoard(onSuccess: (List<Task>) -> Unit) {
        val disposable = taskRetrofit.findAllByBoardId(task.value!!.boardId).subscribe(onSuccess)
        clearOnDestroy(disposable)
    }

    private fun loadBoardBackground(task: Task, view: View) {
        val disposable = taskRetrofit.findBoard(task.id).subscribe { board ->
            trelloUtil.loadBoardBackground(board, view)
        }
        clearOnDestroy(disposable)
    }
}