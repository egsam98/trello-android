package com.project.trello_fintech.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.*
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.project.trello_fintech.BR
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.BoardsAdapter
import com.project.trello_fintech.listeners.BoardTouchHelperCallback
import com.project.trello_fintech.utils.isInternetAvailable
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Фрагмент содержит список (RecyclerView) досок
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property boardsViewModel BoardsViewModel
 */
class BoardsFragment: Fragment(), SwipeRefreshLayout.OnRefreshListener {

    @Inject
    lateinit var activity: MainActivity

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private lateinit var boardsViewModel: BoardsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        MainActivity.component.inject(this)
        boardsViewModel = cleanableViewModelProvider.get(viewLifecycleOwner)
        val binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_boards, container, false)
        binding.setVariable(BR.viewModel, boardsViewModel)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val swipeRefreshLayout = view.findViewById<SwipeRefreshLayout>(R.id.boards_refresh_layout).apply {
            setOnRefreshListener(this@BoardsFragment)
        }

        val boardsAdapter = BoardsAdapter(boardsViewModel)

        with(boardsViewModel) {
            load()
            observe {
                boardsAdapter.setData(it)
            }
            isLoading.observe(viewLifecycleOwner, Observer {
                if (!it) swipeRefreshLayout.isRefreshing = it
            })
        }

        view.findViewById<RecyclerView>(R.id.boards_list).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = boardsAdapter
            ItemTouchHelper(BoardTouchHelperCallback(boardsViewModel)).attachToRecyclerView(this)
        }

        view.findViewById<FloatingActionButton>(R.id.add_board).setOnClickListener {
            if (isInternetAvailable())
                AddBoardDialogFragment().show(childFragmentManager, null)
            else
                boardsViewModel.onError.emit(Pair("Нет подключения к интернету", null))
        }

        activity.supportActionBar?.title = "Доски"
    }

    override fun onRefresh() {
        boardsViewModel.load()
    }
}