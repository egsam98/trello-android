package com.project.homework_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.homework_2.R
import com.project.homework_2.adapters.BoardsAdapter
import com.project.homework_2.presenters.BoardsPresenter


/**
 * Фрагмент содержит список (RecyclerView) досок
 */
class BoardsFragment: Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_boards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            view.findViewById<RecyclerView>(R.id.boards_list).apply {
                layoutManager = LinearLayoutManager(it)
                adapter = BoardsAdapter()
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_board).setOnClickListener {
            AddBoardDialogFragment().show(childFragmentManager, null)
        }

        (activity as AppCompatActivity).supportActionBar?.title = "Доски"
    }

    override fun onPause() {
        super.onPause()
        BoardsPresenter.save(context!!)
    }
}