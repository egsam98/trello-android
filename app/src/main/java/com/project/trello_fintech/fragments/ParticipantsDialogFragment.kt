package com.project.trello_fintech.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.ParticipantsAdapter
import com.project.trello_fintech.adapters.SelectParticipantsAdapter
import com.project.trello_fintech.adapters.UsersAdapter
import com.project.trello_fintech.models.User
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.UsersViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


class ParticipantsDialogFragment: DialogFragment() {

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val usersViewModel by lazy {
        cleanableViewModelProvider.get<UsersViewModel>(this)
    }
    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(this)
    }

    private lateinit var selectParticipantsAdapter: SelectParticipantsAdapter

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)
        val cxt = requireContext()

        selectParticipantsAdapter = SelectParticipantsAdapter()
        val view = RecyclerView(cxt).apply {
            layoutManager = LinearLayoutManager(cxt)
            adapter = selectParticipantsAdapter
        }
        return AlertDialog.Builder(cxt)
            .setTitle(R.string.participants)
            .setView(view)
            .setPositiveButton("Готов"){ _, _ ->
                selectParticipantsAdapter.getSelected()
                selectParticipantsAdapter.onUserSelect.subscribe {
                    // НЕ УСПЕЛ
                }
            }
            .create()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        arguments?.let {
            val boardId = it.getString("boardId")
            val taskId = it.getString("taskId")
            usersViewModel.observeBoardAndTaskUsers(boardId!!, taskId!!) { pair ->
                selectParticipantsAdapter.attachBoardAndTaskUsers(pair)
            }
        }
    }

}