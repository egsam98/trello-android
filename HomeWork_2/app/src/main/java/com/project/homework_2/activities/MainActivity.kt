package com.project.homework_2.activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.project.homework_2.R
import com.project.homework_2.fragments.BoardDetailsFragment
import com.project.homework_2.fragments.BoardsFragment
import com.project.homework_2.presenters.BoardsPresenter


class MainActivity : AppCompatActivity(), BoardsPresenter.IView {

    lateinit var presenter: BoardsPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        presenter = BoardsPresenter(this).apply {
            load(this@MainActivity)
        }

        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .add(R.id.fragment_container, BoardsFragment())
                .commit()
        }
    }

    override fun showDetails(title: String) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, BoardDetailsFragment().apply {
                arguments = Bundle().apply { putString("title", title) }
            })
            .addToBackStack(null)
            .commit()
    }

    override fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
