package com.project.trello_fintech.services

import android.content.Context
import android.widget.Toast
import com.google.firebase.database.*
import com.project.trello_fintech.BuildConfig
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.api.BoardApi
import com.project.trello_fintech.api.RetrofitClient
import com.project.trello_fintech.api.TaskApi
import com.project.trello_fintech.api.UserApi
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.firebase.FirebaseMessage
import com.project.trello_fintech.models.firebase.SessionStart
import com.project.trello_fintech.services.utils.NotificationType
import com.project.trello_fintech.utils.dec
import com.project.trello_fintech.utils.inc
import com.project.trello_fintech.utils.reactive.LiveEvent
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Сервис взаимодействия с Firebase Relatime Database (доп-о Opentok для видеоконференций)
 * @property cxt Context
 * @property authService AuthenticationService
 * @property fcmSenderService FCMSenderService
 * @property database FirebaseDatabase
 * @property onError LiveEvent<Pair<String, Int?>>
 * @property taskApi TaskApi
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

    private val database = FirebaseDatabase.getInstance()
    private val onError = LiveEvent<Pair<String, Int?>>().apply {
        observeForever { (text) ->
            Toast.makeText(cxt, text, Toast.LENGTH_LONG).show()
        }
    }
    private val taskApi by lazy { retrofitClient.create<TaskApi>(onError) }
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

    fun registerBoards(boards: List<Board>) {
        val boardsRef = database.getReference("boards")
        boards.forEach {
            taskApi.findAllByBoardId(it.id).subscribe { tasks ->
                val boardRef = boardsRef.child(it.id)
                boardRef.child("title").setValue(it.title)
                val tasksData = tasks.associateBy({ task -> task.id }, {""})
                boardRef.child("tasks").updateChildren(tasksData)
            }
        }
    }

    fun videoCall(board: Board, func: (SessionStart) -> Unit) {
        database.getReference("boards/${board.id}/stream").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                dataSnapshot.getValue(SessionStart::class.java)?.let {
                    dataSnapshot.incUsersCountAndRunCallback(it)
                } ?: run {
                    opentokApi.createSession()
                        .doOnSuccess {
                            dataSnapshot.ref.setValue(it)
                            sendInvitation(board)
                            dataSnapshot.incUsersCountAndRunCallback(it)
                        }
                        .subscribe()
                }
            }

            override fun onCancelled(err: DatabaseError) {
                showError(err.message)
            }

            private fun DataSnapshot.incUsersCountAndRunCallback(sessionStart: SessionStart) {
                ref.child("usersCount").inc(
                    onCompleteCallback = { func(sessionStart) },
                    onErrorCallback = { err -> showError(err.message) }
                )
            }
        })
    }

    fun videoCall(boardId: String, func: (SessionStart) -> Unit) {
        boardApi.findById(boardId)
            .doOnSuccess { videoCall(it, func) }
            .subscribe()
    }

    fun stopVideoCall(board: Board) {
        val streamRef = database.getReference("boards/${board.id}/stream")
        streamRef.child("usersCount").dec (
            onCompleteCallback = {
                if (it == 0)
                    streamRef.removeValue()
            },
            onErrorCallback = { showError(it.message) }
        )
    }

    private fun sendInvitation(board: Board) {
        val currentUser = authService.user
        val msg = FirebaseMessage.create(currentUser.id, "Видеоконференция", board.id,
            "${currentUser.fullname} приглашает Вас на видеоконференцию!",
            NotificationType.ACCEPTDECLINE)
        fcmSenderService.send(msg)
    }

    private fun showError(msg: String) {
        Toast.makeText(cxt, msg, Toast.LENGTH_LONG).show()
    }
}