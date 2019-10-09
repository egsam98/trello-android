package com.project.homework_2.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.homework_2.R
import com.project.homework_2.activities.MainActivity
import com.project.homework_2.adapters.BoardsAdapter
import com.project.homework_2.presenters.BoardsPresenter


/**
 * Фрагмент содержит список (RecyclerView) досок
 * @property boardsRecyclerView RecyclerView
 * @property presenter BoardsPresenter
 */
class BoardsFragment: Fragment() {

    private lateinit var boardsRecyclerView: RecyclerView
    private lateinit var presenter: BoardsPresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_boards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        boardsRecyclerView = view.findViewById<RecyclerView>(R.id.boards_list).apply {
            layoutManager = LinearLayoutManager(activity)
        }

        view.findViewById<FloatingActionButton>(R.id.add_board).setOnClickListener {
            AddBoardDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        presenter = (activity as MainActivity).presenter
        boardsRecyclerView.adapter = BoardsAdapter(presenter)
    }

    override fun onPause() {
        super.onPause()
        presenter.save(context!!)
    }
}