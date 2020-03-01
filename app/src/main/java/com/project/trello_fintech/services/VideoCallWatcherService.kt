package com.project.trello_fintech.services

import android.app.Service
import android.content.Context
import android.content.Intent
import com.project.trello_fintech.Application
import com.project.trello_fintech.models.Board
import javax.inject.Inject


/**
 * Сервис, используемый для удаления данных сессии видеозвонка в случае закрытия приложения
 * @property firebaseService FirebaseService
 * @property board Board
 */
class VideoCallWatcherService : Service() {
    companion object {
        private const val BOARD_ARG = "board"
        fun start(cxt: Context, board: Board) {
            val intent = Intent(cxt, VideoCallWatcherService::class.java).apply {
                putExtra(BOARD_ARG, board)
            }
            cxt.startService(intent)
        }
        fun stop(cxt: Context) {
            cxt.stopService(Intent(cxt, VideoCallWatcherService::class.java))
        }
    }

    @Inject
    lateinit var firebaseService: FirebaseService

    lateinit var board: Board

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        board = intent.getSerializableExtra(BOARD_ARG) as Board
        Application.component.inject(this)
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        firebaseService.stopVideoCall(board, blocking = true)
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        stopSelf()
    }

    override fun onBind(p0: Intent?) = null
}
