package com.project.trello_fintech.activities

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.*
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.RecyclerView
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexboxLayoutManager
import com.opentok.android.*
import com.project.trello_fintech.Application
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.opentok.SubscribersAdapter
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.firebase.SessionStart
import com.project.trello_fintech.services.AuthenticationService
import com.project.trello_fintech.services.FirebaseService
import com.project.trello_fintech.services.VideoCallWatcherService
import com.project.trello_fintech.services.NotificationService
import com.project.trello_fintech.utils.observe
import com.project.trello_fintech.view_models.VideoCallViewModel
import java.lang.ClassCastException
import java.lang.Exception
import java.lang.NullPointerException
import javax.inject.Inject


/**
 * Активити экрана видеозвонка участников проекта (одной доски)
 * @property session Session?
 * @property board Board?
 * @property publisherViewContainer FrameLayout
 * @property subscriberContainers RecyclerView
 * @property subscribersAdapter SubscribersAdapter
 * @property firebaseService FirebaseService
 * @property notificationService NotificationService
 */
class VideoCallActivity : AppCompatActivity(), Session.SessionListener {
    companion object {
        private const val ACCESS_VIDEOCALL_REQUEST_CODE = 0
        private const val BOARD_ARG = "board"
        private const val BOARD_ID_ARG = "board_id"
        fun start(cxt: Context, board: Board) {
            val intent = Intent(cxt, VideoCallActivity::class.java).apply {
                putExtra(BOARD_ARG, board)
            }
            cxt.startActivity(intent)
        }

        fun createNotificationIntent(cxt: Context, boardId: String, notificationId: Int) =
            NotificationService.createIntent(cxt, VideoCallActivity::class, notificationId).apply {
                putExtra(BOARD_ID_ARG, boardId)
            }
    }

    private var session: Session? = null
    private var publisher: Publisher? = null
    private var board: Board? = null
    private val videoCallViewModel by lazy { ViewModelProviders.of(this).get(VideoCallViewModel::class.java) }

    private lateinit var publisherViewContainer: FrameLayout
    private lateinit var subscriberContainers: RecyclerView
    private lateinit var subscribersAdapter: SubscribersAdapter

    @Inject
    lateinit var firebaseService: FirebaseService

    @Inject
    lateinit var notificationService: NotificationService

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

        if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED)
            initSession()
        else
            requestPermissions(
                arrayOf(Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS),
                ACCESS_VIDEOCALL_REQUEST_CODE
            )

        findViewById<ImageView>(R.id.stop_videocall).apply {
            setOnClickListener { finish() }
        }
    }

    private fun initSession() {
        val callback: (SessionStart) -> Unit = {
            session = Session.Builder(this, it.apiKey, it.sessionId).build().apply {
                setSessionListener(this@VideoCallActivity)
                connect(it.token)
            }
        }

        try{
            board = intent.getSerializableExtra(BOARD_ARG) as Board
            firebaseService.videoCall(board!!) { callback(it) }
            VideoCallWatcherService.start(this, board!!)
        } catch (e: Exception) {
            if (e is NullPointerException || e is ClassCastException) {
                intent.getStringExtra(BOARD_ID_ARG)?.let { boardId ->
                    notificationService.cancelAll()
                    firebaseService.videoCall(boardId) { callback(it) }
                }
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
        publisher = Publisher.Builder(this)
            .name(authService.user.fullname)
            .build().apply {
                renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
            }

        val publisherView = publisher?.view
        publisherViewContainer.addView(publisherView)
        if (publisherView is GLSurfaceView) {
            publisherView.setZOrderOnTop(true)
        }

        session.publish(publisher)
        setupCallControllers()
    }

    // Disconnected from session
    override fun onDisconnected(session: Session) {}

    // New stream received in session
    override fun onStreamReceived(session: Session, stream: Stream) {
        val subscriber = Subscriber.Builder(this, stream).build().apply {
            renderer.setStyle(BaseVideoRenderer.STYLE_VIDEO_SCALE, BaseVideoRenderer.STYLE_VIDEO_FILL)
        }
        session.subscribe(subscriber)
        subscribersAdapter.register(subscriber)
    }

    override fun onStreamDropped(session: Session, stream: Stream) {
        subscribersAdapter.deleteByStream(stream)
    }

    override fun onError(session: Session, opentokError: OpentokError) {
        if (opentokError.isTokenProblem()) {
            board?.let(firebaseService::removeVideoCall)
            initSession()
            return
        }
        val errMsg = "onError: " + opentokError.errorDomain + " : " +
                opentokError.errorCode + " - " + opentokError.message + " in session: " + session.sessionId
        Toast.makeText(this, errMsg, Toast.LENGTH_LONG).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        session?.disconnect()
        VideoCallWatcherService.stop(this)
    }

    private fun setupCallControllers() {
        findViewById<ImageView>(R.id.mute_mic).apply {
            setOnClickListener { videoCallViewModel.turnOnOffMic() }
            videoCallViewModel.micState.observe(this@VideoCallActivity) {
                publisher?.publishAudio = it.isOn
                setBackgroundResource(it.icon)
            }
        }

        findViewById<ImageView>(R.id.mute_video).apply {
            setOnClickListener { videoCallViewModel.turnOnOffVideo() }
            videoCallViewModel.videoState.observe(this@VideoCallActivity) {
                publisher?.publishVideo = it.isOn
                val visibility = if (it.isOn) View.VISIBLE else View.GONE
                publisher?.view?.visibility = visibility
                publisherViewContainer.visibility = visibility
                setBackgroundResource(it.icon)
            }
        }
    }

    private fun OpentokError.isTokenProblem() = message.contains("token", ignoreCase = true)
}