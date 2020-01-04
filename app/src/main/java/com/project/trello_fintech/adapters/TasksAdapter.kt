package com.project.trello_fintech.adapters

import android.view.*
import android.widget.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woxthebox.draglistview.DragItemAdapter
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.utils.ResettableFilter
import com.project.trello_fintech.utils.ResettableFilterable
import com.project.trello_fintech.utils.toShortFormat
import com.project.trello_fintech.view_models.TasksViewModel


/**
 * Адаптер списка задач
 * @property column Column
 * @property tasksViewModel TasksViewModel
 * @property maxAttachmentsPreviewNum Int
 * @property data List<Task>
 */
class TasksAdapter(val column: Column, private val tasksViewModel: TasksViewModel, private val maxAttachmentsPreviewNum: Int):
    DragItemAdapter<Task, TasksAdapter.TaskViewHolder>(), ResettableFilterable {

   var data = listOf<Task>()
    set(value) {
        field = value
        itemList = value
    }


    class TaskViewHolder(val view: View) : DragItemAdapter.ViewHolder(view, R.id.task, true) {
        val layout: LinearLayout = view.findViewById(R.id.wrapper)
        val titleView: TextView = view.findViewById(R.id.task_title)
        val datesView: TextView = view.findViewById(R.id.task_dates)
        val attachmentsPreView: RecyclerView = view.findViewById<RecyclerView>(R.id.attachments_preview).apply {
            layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, false)
        }
        val plusNAttachmentsAlsoView: TextView = view.findViewById(R.id.plus_n_attachments_also)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_list_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        with(holder) {
            val item = itemList[position]
            titleView.text = item.text

            val dates = arrayOf(item.creationDate, item.dueDate).joinToString(" - ") { it.toShortFormat() }
            datesView.text = dates

            view.setOnClickListener {
                tasksViewModel.onClick.emit(item)
            }
            with(item.attachments) {
                val imageAttachments = asSequence().filter { it.isImage() }.take(2).toList()
                attachmentsPreView.adapter = AttachmentsPreviewAdapter(imageAttachments)
                if (size > maxAttachmentsPreviewNum) {
                    val text = "+ еще ${size - maxAttachmentsPreviewNum}"
                    plusNAttachmentsAlsoView.text = text
                }
            }
        }
    }

    override fun getUniqueItemId(position: Int) = itemList[position].id.hashCode().toLong()

    override fun getFilter(): ResettableFilter = object: ResettableFilter() {
        override fun performFiltering(searchText: CharSequence): FilterResults? {
            val found = data.filter { it.text.toLowerCase() == searchText.toString().toLowerCase() }
            return FilterResults().apply {
                values = found
                count = found.size
            }
        }

        override fun publishResults(searchText: CharSequence, filterResults: FilterResults) {
            itemList = filterResults.values as List<Task>
        }

        override fun reset() {
            itemList = data
        }
    }
}