package com.project.trello_fintech.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.opentok.android.*
import com.project.trello_fintech.Application
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.opentok.SubscribersAdapter
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.services.AuthenticationService
import com.project.trello_fintech.services.FirebaseService
import javax.inject.Inject


/**
 * Активити экрана видеозвонка участников проекта (одной доски)
 * @property session Session
 * @property publisherViewContainer FrameLayout
 * @property subscriberContainers RecyclerView
 * @property subscribersAdapter SubscribersAdapter
 * @property firebaseService FirebaseService
 */
class VideoCallActivity : AppCompatActivity(), Session.SessionListener {
    companion object {
        private const val ACCESS_VIDEOCALL_REQUEST_CODE = 0
        private const val BOARD_ARG = "board_id"
        fun start(cxt: Context, board: Board) {
            val intent = Intent(cxt, VideoCallActivity::class.java).apply {
                putExtra(BOARD_ARG, board)
            }
            cxt.startActivity(intent)
        }
    }

    private var session: Session? = null
    private lateinit var publisherViewContainer: FrameLayout
    private lateinit var subscriberContainers: RecyclerView
    private lateinit var subscribersAdapter: SubscribersAdapter

    @Inject
    lateinit var firebaseService: FirebaseService

    @Inject
    lateinit var authService: AuthenticationService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_videocall)
        Application.component.inject(this)

        publisherViewContainer = findViewById(R.id.publisher_container)
        subscribersAdapter = SubscribersAdapter()
        subscriberContainers = findViewById<RecyclerView>(R.id.subscriber_containers).apply {
            layoutManager = FlexboxLayoutManager(this@VideoCallActivity, FlexDirection.ROW)
            adapter = subscribersAdapter
        }

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
            initSession()
        else
            requestPermissions(arrayOf(Manifest.permission.CAMERA, Manifest.permission.MODIFY_AUDIO_SETTINGS),
                ACCESS_VIDEOCALL_REQUEST_CODE)
    }

    private fun initSession() {
        val board = intent.getSerializableExtra(BOARD_ARG) as Board
        firebaseService.videoCall(board) {
            session = Session.Builder(this, it.apiKey, it.sessionId).build().apply {
                setSessionListener(this@VideoCallActivity)
                connect(it.token)
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == ACCESS_VIDEOCALL_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            initSession()
    }

    // Connected to session
    override fun onConnected(session: Session) {
        val publisher = Publisher.Builder(this)
            .name(authService.user.fullname)
            .audioTrack(false)
            .build().apply {
                renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            }

        val publisherView = publisher.view
        publisherViewContainer.addView(publisherView)
        if (publisherView is GLSurfaceView) {
            publisherView.setZOrderOnTop(true)
        }

        session.publish(publisher)
    }

    // TODO: remove stream data if last user disconnected
    // Disconnected from session
    override fun onDisconnected(session: Session) {}

    // New stream received in session
    override fun onStreamReceived(session: Session, stream: Stream) {
        val subscriber = Subscriber.Builder(this, stream).build().apply {
            renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
        }
        session.subscribe(subscriber)
        subscribersAdapter.register(subscriber, stream.name)
    }

    override fun onStreamDropped(session: Session, stream: Stream) {
        subscribersAdapter.deleteByStream(stream)
    }

    // TODO: handle token error
    override fun onError(session: Session, opentokError: OpentokError) {
        val errMsg = "onError: " + opentokError.errorDomain + " : " +
                opentokError.errorCode + " - " + opentokError.message + " in session: " + session.sessionId
        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        session?.disconnect()
    }
}