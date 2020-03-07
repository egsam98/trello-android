package com.project.trello_fintech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.trello_fintech.Application
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveList
import org.json.JSONObject
import java.net.URLEncoder
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.utils.TrelloUtil
import com.project.trello_fintech.utils.hideSystemToolbar
import org.json.JSONArray
import javax.inject.Inject


/**
 * Chrome браузер со слушателем сообщений в консоли (уровень "warning" игнорируется)
 * @property cxt Context
 */
class ErrorListenerClient(private val cxt: Context): WebChromeClient() {
    override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
        if (consoleMessage.messageLevel() != ConsoleMessage.MessageLevel.WARNING)
            Toast.makeText(cxt, consoleMessage.message(), Toast.LENGTH_LONG).show()
        return super.onConsoleMessage(consoleMessage)
    }
}

/**
 * Диаграмма Гантта для выбранной доски задач
 */
class GanttChartActivity : AppCompatActivity() {
    companion object {
        private const val TASKS_ARG = "tasks"
        private const val BOARD_TITLE_ARG = "board_title"
        fun start(activity: Activity, board: Board, tasks: HashMap<Column, LiveList<Task>>) {
            val hashMap = tasks.entries.associateTo(HashMap()) { (column, tasks) -> Pair(column, tasks.data) }
            val intent = Intent(activity, GanttChartActivity::class.java).apply {
                putExtra(TASKS_ARG, hashMap)
                putExtra(BOARD_TITLE_ARG, board.title)
            }
            activity.startActivity(intent)
        }
    }

    @Inject
    lateinit var trelloUtil: TrelloUtil

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Application.component.inject(this)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            isVerticalScrollBarEnabled = true
            webChromeClient = ErrorListenerClient(this@GanttChartActivity)
        }
        setContentView(webView)

        val title = intent.getStringExtra(BOARD_TITLE_ARG)
        val data = intent.getSerializableExtra(TASKS_ARG) as Map<Column, List<Task>>
        val jsonEncoded = URLEncoder.encode(data.toJSONString(), "utf-8")
        webView.loadUrl("file:///android_asset/anychart-gantt.html?title=$title&json=${jsonEncoded}")

        supportActionBar?.setTitle(R.string.Gantt)
        hideSystemToolbar()
    }

    override fun onBackPressed() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        super.onBackPressed()
    }

    private fun Map<Column, List<Task>>.toJSONString(): String {
        val jsonArray = JSONArray()
        keys.forEach {
            jsonArray.put(JSONObject()
                .put("id", it.id)
                .put("name", it.title))
        }
        forEach { (column, tasks) ->
            val tasksJSON = tasks.map {
                val percent = trelloUtil.getProgressPercent(it)
                val dependantsJSONArray = JSONArray()
                trelloUtil.getDependants(it)
                    .map { id -> JSONObject().put("connectTo", id) }
                    .forEach { dependantJSON -> dependantsJSONArray.put(dependantJSON) }
                JSONObject()
                    .put("id", it.id)
                    .put("name", it.text)
                    .put("parent", column.id)
                    .put("actualStart", it.creationDate)
                    .put("actualEnd", it.dueDate?: it.creationDate)
                    .put("progressValue", "$percent%")
                    .put("connector", dependantsJSONArray)
            }
            tasksJSON.forEach { jsonArray.put(it) }
        }
        return jsonArray.toString()
    }
}
