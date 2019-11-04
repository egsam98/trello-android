package com.project.trello_fintech.adapters

import android.view.*
import android.widget.TextView
import com.woxthebox.draglistview.DragItemAdapter
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task


/**
 * Адаптер списка задач
 * @property column Column
 * @constructor
 */
class TasksAdapter(val column: Column):
    DragItemAdapter<Task, TasksAdapter.TaskViewHolder>() {

    class TaskViewHolder(val view: TextView): DragItemAdapter.ViewHolder(view, R.id.task, true)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false) as TextView
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        holder.view.text = itemList[position].text
    }

    override fun getUniqueItemId(position: Int) = itemList[position].id.hashCode().toLong()

}