package com.project.homework_2.adapters

import android.view.*
import android.widget.ImageButton
import android.widget.TextView
import com.woxthebox.draglistview.DragItemAdapter
import com.project.homework_2.R
import com.project.homework_2.models.Task
import com.project.homework_2.presenters.TasksPresenter


/**
 * Адаптер списка задач
 * @see com.project.homework_2.fragments.TasksFragment
 * @property presenter TasksPresenter
 */
class TasksAdapter(private val presenter: TasksPresenter):
    DragItemAdapter<Task, TasksAdapter.TaskViewHolder>(), TasksPresenter.IView {

    class TaskViewHolder(val view: View): DragItemAdapter.ViewHolder(view, R.id.task, true)

    init {
        itemList = presenter.tasks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder.view){
            findViewById<TextView>(R.id.task).text = itemList[position].text
            findViewById<ImageButton>(R.id.delete_task).setOnClickListener {
                presenter.removeAt(position)
            }
        }
    }

    override fun getUniqueItemId(position: Int) = itemList[position].id

    override fun onTasksChange() {
        notifyDataSetChanged()
    }
}