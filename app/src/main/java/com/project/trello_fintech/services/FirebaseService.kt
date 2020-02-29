package com.project.trello_fintech.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.firestore.*
import com.google.firebase.messaging.FirebaseMessaging
import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.api.BoardApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.UserApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.models.firebase.FirebaseMessage
import com.project.trello_fintech.models.firebase.SessionStart
import com.project.trello_fintech.services.utils.NotificationType
import com.project.trello_fintech.utils.*
import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton


private const val TOPIC_NAME = "/topics"

/**
 * Сервис взаимодействия с Firebase Relatime Database (доп-о Opentok для видеоконференций)
 * @property cxt Context
 * @property authService AuthenticationService
 * @property fcmSenderService FCMSenderService
 * @property db FirebaseFirestore
 * @property boardsCollection CollectionReference
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property userApi UserApi
 * @property boardApi BoardApi
 * @property opentokApi (com.project.trello_fintech.services.FirebaseService.OpentokApi..com.project.trello_fintech.services.FirebaseService.OpentokApi?)
 */
@Singleton
class FirebaseService @Inject constructor(
        private val cxt: Context,
        private val authService: AuthenticationService,
        private val fcmSenderService: FCMSenderService,
        gsonConverterFactory: GsonConverterFactory,
        retrofitClient: RetrofitClient
    ) {

    private val db = FirebaseFirestore.getInstance()
    private val boardsCollection = db.collection("boards")
    private val onError = LiveEvent<Pair<String, Int?>>().apply {
        observeForever { (text) ->
            Toast.makeText(cxt, text, Toast.LENGTH_LONG).show()
        }
    }
    private val userApi by lazy { retrofitClient.create<UserApi>(onError) }
    private val boardApi by lazy { retrofitClient.create<BoardApi>(onError) }

    private interface OpentokApi {
        @GET("createSession")
        fun createSession(): Single<SessionStart>
    }

    private val opentokApi by lazy {
        Retrofit.Builder()
            .addConverterFactory(gsonConverterFactory)
            .addCallAdapterFactory(RxJava2Adapter(AndroidSchedulers.mainThread()))
            .baseUrl(BuildConfig.OPENTOK_SERVER_BASE_URL)
            .build()
            .create(OpentokApi::class.java)
    }

    fun registerBoard(board: Board) {
        val boardRef = boardsCollection.document(board.id)
        boardRef.setField("title", board.title)
            .addOnSuccessListener {
                FirebaseMessaging.getInstance().subscribeToTopic("${TOPIC_NAME}/${board.id}")
            }
            .addOnFailureListener(Exception::show)
    }

    fun deleteBoard(board: Board) {
        boardsCollection.document(board.id).delete()
    }

    fun deleteTask(task: Task) {
        getTask(task) { it.reference.delete() }
    }

    fun videoCall(board: Board, func: (SessionStart) -> Unit) {
        getSession(board) { sessionStart, document ->
            sessionStart?.let {
                document.reference.incFields("usersCount")
                func(it)
            }?: run {
                opentokApi.createSession().doOnSuccess {
                    document.reference.set(mapOf(
                        "usersCount" to 1,
                        "session" to it
                    ), SetOptions.merge()).addOnFailureListener { e -> e.show() }
                    sendInvitation(board)
                    func(it)
                }
                .subscribe()
            }
        }
    }

    fun videoCall(boardId: String, func: (SessionStart) -> Unit) {
        boardApi.findById(boardId)
            .doOnSuccess { videoCall(it, func) }
            .subscribe()
    }

    fun stopVideoCall(board: Board) {
        getSession(board) {_, document ->
            document.getLong("usersCount")
                ?.let {
                    if (it <= 1)
                        document.reference.deleteFields("session", "usersCount")
                    else
                        document.reference.decFields("usersCount")
                }?:
                    document.reference.deleteFields("session")
        }
    }

    fun removeVideoCall(board: Board) {
        getSession(board) { _, document -> document.reference.deleteFields("session") }
    }

    private fun getSession(board: Board,
                           callback: (SessionStart?, document: DocumentSnapshot) -> Unit) {
        val boardRef = boardsCollection.document(board.id)
        boardRef.get()
            .addOnSuccessListener { document ->
                val sessionStart = document.get("session", SessionStart::class.java)
                callback(sessionStart, document)
            }
            .addOnFailureListener { it.show() }
    }

    fun getTask(task: Task, onSuccess: (DocumentSnapshot) -> Unit) {
        boardsCollection.document("${task.boardId}/tasks/${task.id}").get()
            .addOnSuccessListener { onSuccess(it) }
    }

    private fun sendInvitation(board: Board) {
        val currentUser = authService.user
        val msgData = FirebaseMessage.Data(
            fromId = currentUser.id,
            notificationType = NotificationType.ACCEPTDECLINE,
            boardId = board.id,
            title = "Видеоконференция",
            body = "${currentUser.fullname} приглашает Вас на видеоконференцию!")
        val msg = FirebaseMessage("$TOPIC_NAME/${board.id}", msgData)
        fcmSenderService.send(msg)
    }
}