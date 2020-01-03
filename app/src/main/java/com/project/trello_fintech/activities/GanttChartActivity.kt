package com.project.trello_fintech.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.webkit.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.reactive.LiveList
import org.json.JSONObject
import java.net.URLEncoder
import com.project.trello_fintech.R


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
        fun start(activity: Activity, tasks: HashMap<Column, LiveList<Task>>) {
            val hashMap = tasks.entries.associateTo(HashMap()) { (column, tasks) -> Pair(column, tasks.data) }
            val intent = Intent(activity, GanttChartActivity::class.java).apply {
                putExtra(TASKS_ARG, hashMap)
            }
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_LANDSCAPE
        val webView = WebView(this).apply {
            settings.javaScriptEnabled = true
            webChromeClient = ErrorListenerClient(this@GanttChartActivity)
        }
        setContentView(webView)

        val data = intent.getSerializableExtra(TASKS_ARG) as Map<Column, List<Task>>
        val jsonEncoded = URLEncoder.encode(data.toJSONString(), "utf-8")
        webView.loadUrl("file:///android_asset/anychart-gantt.html?json=${jsonEncoded}")

        supportActionBar?.setTitle(R.string.Gantt)
    }

    override fun onBackPressed() {
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_USER_PORTRAIT
        super.onBackPressed()
    }

    private fun Map<Column, List<Task>>.toJSONString(): String {
        val json = JSONObject()
        forEach { (column, tasks) ->
            val tasksJson = tasks.map {
                JSONObject().apply {
                    put("id", it.id)
                    put("text", it.text)
                    put("creationDate", it.creationDate)
                    put("dueDate", it.dueDate?: it.creationDate)
                }
            }
            json.put(column.title, tasksJson)
        }
        return json.toString()
    }
}
