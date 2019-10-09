package com.project.homework_2.fragments

import android.content.Context
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


/**
 * Фрагмент содержит список (RecyclerView) досок
 * @property activity MainActivity?
 */
class BoardsFragment: Fragment() {

    private var activity: MainActivity? = null

    override fun onAttach(context: Context?) {
        super.onAttach(context)
        if (context is MainActivity){
            activity = context
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_boards, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        activity?.let {
            view.findViewById<RecyclerView>(R.id.boards_list).apply {
                layoutManager = LinearLayoutManager(activity)
                adapter = BoardsAdapter(it.presenter)
            }
        }

        view.findViewById<FloatingActionButton>(R.id.add_board).setOnClickListener {
            AddBoardDialogFragment().show(childFragmentManager, null)
        }
    }

    override fun onPause() {
        super.onPause()
        activity?.let { it.presenter.save(it) }
    }
}