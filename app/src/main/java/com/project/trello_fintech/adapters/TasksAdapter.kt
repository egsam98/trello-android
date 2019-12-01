package com.project.trello_fintech.adapters

import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.woxthebox.draglistview.DragItemAdapter
import com.project.trello_fintech.R
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TasksViewModel
import io.reactivex.subjects.*


/**
 * Адаптер списка задач
 * @property column Column
 * @property tasksViewModel TasksViewModel
 * @property maxAttachmentsPreviewNum Int
 * @property searchText String
 * @property foundCount Int максимальное кол-во отображаемых превью-изображений
 * @property onSearchFinish PublishSubject<(kotlin.Int..kotlin.Int?)>
 */
class TasksAdapter(val column: Column, private val tasksViewModel: TasksViewModel, private val maxAttachmentsPreviewNum: Int):
    DragItemAdapter<Task, TasksAdapter.TaskViewHolder>(), ISearchableAdapter<TasksAdapter.TaskViewHolder> {

    private var searchText: String = ""
    private var foundCount = 0

    override val onSearchFinish = PublishSubject.create<Int>()

    class TaskViewHolder(val view: View) : DragItemAdapter.ViewHolder(view, R.id.task, true) {
        val layout: LinearLayout = view.findViewById(R.id.wrapper)
        val textView: TextView = view.findViewById(R.id.task_title)
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
            textView.text = item.text

            view.setOnClickListener {
                tasksViewModel.onClick.emit(item.id)
            }
            with(item.attachments) {
                val imageAttachments = asSequence().filter { it.isImage() }.take(2).toList()
                attachmentsPreView.adapter = AttachmentsPreviewAdapter(imageAttachments)
                if (size > maxAttachmentsPreviewNum) {
                    val text = "+ еще ${size - maxAttachmentsPreviewNum}"
                    plusNAttachmentsAlsoView.text = text
                }
            }

            if (searchText.isNotEmpty()) {
                if (textView.text.toString().toLowerCase() == searchText.toLowerCase())
                    onMatch(holder)
                else
                    onMismatch(holder)
                if (position == itemCount - 1) {
                    onSearchFinish.onNext(foundCount)
                    searchText = ""
                    foundCount = 0
                }
            }
        }
    }

    override fun getUniqueItemId(position: Int) = itemList[position].id.hashCode().toLong()

    override fun search(text: String) {
        searchText = text
        notifyDataSetChanged()
    }

    override fun onMatch(holder: TaskViewHolder) {
        holder.layout.setBackgroundResource(R.drawable.red_border)
        foundCount++
    }

    override fun onMismatch(holder: TaskViewHolder) {
        holder.layout.setBackgroundResource(R.drawable.grey_border)
    }
}