package com.project.trello_fintech.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.daimajia.numberprogressbar.NumberProgressBar
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.R
import com.project.trello_fintech.views.TaskDetailSectionView


/**
 * Адаптер списков действий для выполнения одной задачи
 */
object ChecklistsAdapter: RecyclerView.Adapter<ChecklistsAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val sectionView: TaskDetailSectionView = view.findViewById(R.id.title)
        val checklistProgressBar: NumberProgressBar = view.findViewById(R.id.checklist_progress)
        val checkitems: RecyclerView = view.findViewById(R.id.checkitems)
    }

    var data: List<Checklist> = listOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checklist_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checklist = data[position]
        with(holder) {
            sectionView.setText(checklist.title)
            sectionView.setOnClickListener {
                checkitems.visibility = if (checkitems.isShown) View.GONE else View.VISIBLE
            }
            checkitems.layoutManager = LinearLayoutManager(checkitems.context)
            checkitems.adapter = CheckitemAdapter(checklistProgressBar).apply { data = checklist.items }
        }
    }

    override fun getItemCount(): Int = data.size
}


/**
 * Адаптер одного списка действий
 * @property checklistProgressBar NumberProgressBar
 * @property data List<Item>
 * @property checkedCount Int
 */
class CheckitemAdapter(private val checklistProgressBar: NumberProgressBar): RecyclerView.Adapter<CheckitemAdapter.ViewHolder>() {
    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkbox)
        val textView: TextView = view.findViewById(R.id.text)
    }

    var data: List<Checklist.Item> = listOf()
    private var checkedCount = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.checkitem_list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val checkitem = data[position]
        with (holder) {
            textView.text = checkitem.title
            checkBox.isChecked = checkitem.isChecked
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                checkedCount += if (isChecked) 1 else -1
                checklistProgressBar.progress = (checkedCount.toFloat() / itemCount.toFloat() * 100).toInt()
            }
            textView.setOnClickListener {
                checkBox.isChecked = !checkBox.isChecked
            }
        }
    }

    override fun getItemCount(): Int = data.size
}