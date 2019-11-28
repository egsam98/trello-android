package com.project.trello_fintech.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.content.Context
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.TaskHistoryAdapter
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Фрагмент истории изменений задачи
 * @property cxt Context
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property taskDetailViewModel TaskDetailViewModel
 */
class TaskHistoryFragment: Fragment() {
    @Inject
    lateinit var cxt: Context

    @Inject
    lateinit var activity: MainActivity

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val taskDetailViewModel by lazy {
        val taskDetailFragment = activity.supportFragmentManager.findFragmentByTag("taskDetail")!!
        cleanableViewModelProvider.get<TaskDetailViewModel>(taskDetailFragment)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? =
        inflater.inflate(R.layout.fragment_task_history, container, false)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        MainActivity.component.inject(this)

        val taskHistoryAdapter = TaskHistoryAdapter()
        view.findViewById<RecyclerView>(R.id.history_list).apply {
            layoutManager = LinearLayoutManager(cxt)
            adapter = taskHistoryAdapter
        }

        taskDetailViewModel.historyList.observe(this, Observer {
            taskHistoryAdapter.data = it
        })
    }
}