package com.project.trello_fintech.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.trello_fintech.BR
import com.project.trello_fintech.R
import com.project.trello_fintech.adapters.BoardsAdapter
import com.project.trello_fintech.listeners.BoardTouchHelperCallback
import com.project.trello_fintech.listeners.BoardsChangeCallback
import com.project.trello_fintech.view_models.BoardsViewModel


/**
 * Фрагмент содержит список (RecyclerView) досок
 * @property boardsViewModel BoardsViewModel
 */
class BoardsFragment: Fragment() {

    private val boardsViewModel by lazy {
        ViewModelProviders.of(requireActivity()).get(BoardsViewModel::class.java)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_boards, container, false)
        binding.setVariable(BR.viewModel, boardsViewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val boardsAdapter = BoardsAdapter(boardsViewModel)

        boardsViewModel.load()

        boardsViewModel.observe(this, Observer { (before, after) ->
            boardsAdapter.data = after
            val callback = BoardsChangeCallback(before, after)
            DiffUtil.calculateDiff(callback).dispatchUpdatesTo(boardsAdapter)
        })

        view.findViewById<RecyclerView>(R.id.boards_list).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = boardsAdapter
            ItemTouchHelper(BoardTouchHelperCallback(boardsViewModel)).attachToRecyclerView(this)
        }

        view.findViewById<FloatingActionButton>(R.id.add_board).setOnClickListener {
            AddBoardDialogFragment().show(childFragmentManager, null)
        }

        (activity as AppCompatActivity).supportActionBar?.title = "Доски"
    }
}