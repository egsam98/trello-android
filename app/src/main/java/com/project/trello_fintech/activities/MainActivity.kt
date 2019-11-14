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
import com.project.trello_fintech.R
import com.project.trello_fintech.fragments.TasksFragment
import com.project.trello_fintech.fragments.BoardsFragment
import com.project.trello_fintech.fragments.WebViewFragment
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.utils.StringsRepository
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider


class MainActivity : AppCompatActivity() {

    private val boardsViewModel by lazy {
        CleanableViewModelProvider.get<BoardsViewModel>(this)
    }

    private val tasksViewModel by lazy {
        CleanableViewModelProvider.get<TasksViewModel>(this)
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

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

        for (onError in arrayOf(boardsViewModel.onError, tasksViewModel.onError)) {
            onError.observe(this@MainActivity, Observer {(msg, code) ->
                if ((msg.contains("token") || code == 401) && (savedInstanceState == null))
                    openWebViewForToken()
                else
                    showError(msg, code)
            })
        }

        findViewById<Button>(R.id.trello_logout).setOnClickListener {
            StringsRepository.delete("token")
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
            .add(R.id.fragment_container, TasksFragment().apply {
                arguments = Bundle().apply { putSerializable("board", board) }
            })
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
}
