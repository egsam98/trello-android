package com.project.trello_fintech.adapters

import android.view.*
import android.widget.TextView
import com.woxthebox.draglistview.DragItemAdapter
import com.project.homework_2.R
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.presenters.TasksPresenter


/**
 * Адаптер списка задач
 * @see com.project.homework_2.fragments.TasksFragment
 * @property presenter TasksPresenter
 */
class TasksAdapter(private val presenter: TasksPresenter):
    DragItemAdapter<Task, TasksAdapter.TaskViewHolder>(), TasksPresenter.IAdapter {

    class TaskViewHolder(val view: TextView): DragItemAdapter.ViewHolder(view, R.id.task, true)

    init {
        itemList = presenter.tasks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false) as TextView
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.text = itemList[position].text
    }

    override fun getUniqueItemId(position: Int) = itemList[position].id

    override fun onTasksChange() {
        notifyDataSetChanged()
    }

    override fun getPresenter() = presenter
}