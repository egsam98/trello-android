package com.project.trello_fintech.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.BoardsAdapter
import com.project.trello_fintech.listeners.BoardTouchHelperCallback
import com.project.trello_fintech.listeners.BoardsChangeCallback
import com.project.trello_fintech.presenters.BoardsPresenter
import io.reactivex.disposables.Disposable


/**
 * Фрагмент содержит список (RecyclerView) досок
 * @property dataSubscription Disposable подписка на получения пары старого и обновленного списка досок и категорий
 */
class BoardsFragment: Fragment() {

    private lateinit var dataSubscription: Disposable

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_boards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardsAdapter = BoardsAdapter().apply {
            dataSubscription = BoardsPresenter.observe().subscribe { (before, after) ->
                data = after
                val callback = BoardsChangeCallback(before, after)
                DiffUtil.calculateDiff(callback).dispatchUpdatesTo(this)
            }
        }

        activity?.let {
            view.findViewById<RecyclerView>(R.id.boards_list).apply {
                layoutManager = LinearLayoutManager(it)
                adapter = boardsAdapter
                ItemTouchHelper(BoardTouchHelperCallback()).attachToRecyclerView(this)
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

    override fun onDestroyView() {
        super.onDestroyView()
        dataSubscription.dispose()
    }
}