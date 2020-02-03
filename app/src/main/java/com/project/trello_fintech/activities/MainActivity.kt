package com.project.trello_fintech.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import com.google.android.material.navigation.NavigationView
import com.project.trello_fintech.Application
import com.project.trello_fintech.R
import com.project.trello_fintech.di.components.MainActivityComponent
import com.project.trello_fintech.di.modules.MainActivityModule
import com.project.trello_fintech.fragments.*
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import com.project.trello_fintech.utils.StringsRepository
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.UsersViewModel
import javax.inject.Inject


class MainActivity : AppCompatActivity() {
    companion object {
        lateinit var component: MainActivityComponent
    }

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    @Inject
    lateinit var stringsRepository: StringsRepository

    val navigationView: NavigationView by lazy { findViewById<NavigationView>(R.id.nav_view) }

    private val boardsViewModel by lazy {
        cleanableViewModelProvider.get<BoardsViewModel>(this)
    }
    private val tasksViewModel by lazy {
        cleanableViewModelProvider.get<TasksViewModel>(this)
    }
    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(this)
    }
    private val userViewModel by lazy {
        cleanableViewModelProvider.get<UsersViewModel>(this)
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        component = Application.component.plusMainActivityComponent(MainActivityModule(this)).apply {
            inject(this@MainActivity)
        }

        if (savedInstanceState == null)
            supportFragmentManager.beginTransaction()
                .add(R.id.fragment_container, BoardsFragment())
                .commit()

        drawerLayout = findViewById<DrawerLayout>(R.id.drawer_layout).apply {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        boardsViewModel.onClick.observe(this, Observer {
            showTasks(it)
        })

        taskDetailViewModel.onAttachmentClick.observe(this, Observer {
            showAttachmentFullScreen(it)
        })
        taskDetailViewModel.onOpenHistory.observe(this, Observer {
            showTaskHistory()
        })


        for (onError in arrayOf(
            boardsViewModel.onError,
            tasksViewModel.onError,
            taskDetailViewModel.onError,
            userViewModel.onError
        )) {
            onError.observe(this@MainActivity, Observer {(msg, code) ->
                if ((msg.contains("token") || code == 401) && (savedInstanceState == null))
                    openWebViewForToken()
                else
                    showError(msg, code)
            })
        }

        findViewById<Button>(R.id.trello_logout).setOnClickListener {
            stringsRepository.delete("token")
            drawerLayout.closeDrawers()
            openWebViewForToken()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.drawer) {
            with(drawerLayout) {
                if (isDrawerOpen(GravityCompat.END)) {
                    closeDrawers()
                } else {
                    openDrawer(GravityCompat.END, true)
                }
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun showTasks(board: Board) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, TasksFragment.create(board))
            .addToBackStack(null)
            .commit()
    }

    fun showTaskDetail(task: Task) {
        val fragment = TaskDetailFragment.create(task)
        attachTaskDetailFragment(fragment)
    }

    fun showTaskDetail(taskId: String) {
        val fragment = TaskDetailFragment.create(taskId)
        attachTaskDetailFragment(fragment)
    }

    private fun attachTaskDetailFragment(taskDetailFragment: TaskDetailFragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, taskDetailFragment, "taskDetail")
            .addToBackStack(null)
            .commit()
    }

    private fun showAttachmentFullScreen(attachment: Task.Attachment) {
        supportFragmentManager
            .beginTransaction()
            .add(R.id.fragment_container, AttachmentFullScreenFragment.create(attachment))
            .addToBackStack(null)
            .commit()
    }

    private fun showTaskHistory() {
        supportFragmentManager.beginTransaction()
            .add(R.id.fragment_container, TaskHistoryFragment())
            .addToBackStack(null)
            .commit()
    }

    private fun showError(message: String, code: Int?) {
        Toast.makeText(this, "Error ${code?: ""}: $message", Toast.LENGTH_LONG).show()
    }

    private fun openWebViewForToken() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, WebViewFragment())
            .addToBackStack(null)
            .commit()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        val taskDetailFragment = supportFragmentManager.findFragmentByTag("taskDetail") as TaskDetailFragment
        taskDetailFragment.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}
