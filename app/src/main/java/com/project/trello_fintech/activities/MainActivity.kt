package com.project.trello_fintech.activities

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.RxJava2Adapter
import com.project.trello_fintech.fragments.TasksFragment
import com.project.trello_fintech.fragments.BoardsFragment
import com.project.trello_fintech.fragments.WebViewFragment
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.utils.StringsRepository
import okhttp3.Cache
import retrofit2.HttpException


class MainActivity : AppCompatActivity() {

    companion object {
        var cache: Cache? = null
            private set
    }

    private val boardsViewModel by lazy {
        ViewModelProviders.of(this).get(BoardsViewModel::class.java)
    }

    private lateinit var drawerLayout: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        RxJava2Adapter.errorHandler = {
            when(it){
                is HttpException -> {
                    if (it.code() == 401)
                        openWebViewForToken()
                    else {
                        val message = it.response()?.errorBody()?.string() ?: it.response()?.message() ?: it.message()
                        showError(message, it.code())
                    }
                }
                else -> showError(it.message.orEmpty(), null)
            }
        }

        cache = Cache(cacheDir, 4096)
        StringsRepository.attach(getPreferences(Context.MODE_PRIVATE))

        drawerLayout = findViewById<DrawerLayout>(R.id.fragment_container).apply {
            setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
        }

        if (savedInstanceState == null) {
            if (StringsRepository.contains("token"))
                supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, BoardsFragment())
                    .commit()
            else
                openWebViewForToken()
        }

        boardsViewModel.onClick.observe(this, Observer {
            showTasks(it)
        })

        boardsViewModel.onError.observe(this, Observer { (msg, code) ->
            showError(msg, code)
        })
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
            .commit()
    }
}
