package com.project.trello_fintech.adapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Filter
import android.widget.TextView
import com.project.trello_fintech.models.Task


class AutoCompleteTasksAdapter(
    private val cxt: Context,
    private val tasks: List<Task>): ArrayAdapter<Task>(cxt, 0, tasks) {

    private val filteredTasks = mutableListOf<Task>()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        if (convertView == null) {
            view = TextView(cxt).apply {
                setPadding(25, 25, 25, 25)
            }
        }
        (view as TextView).text = getItem(position).text
        return view
    }

    override fun getFilter(): Filter = object: Filter() {
            override fun performFiltering(text: CharSequence?): FilterResults {
                val foundTasks = text?.let {
                    tasks.filter { task -> task.text.startsWith(it, ignoreCase = true) }
                }?: emptyList()
                return FilterResults().apply {
                    values = foundTasks
                    count = foundTasks.size
                }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(text: CharSequence?, results: FilterResults) {
                filteredTasks.clear()
                if (results.count > 0) {
                    filteredTasks.addAll(results.values as Collection<Task>)
                    notifyDataSetChanged()
                }
            }
        }

    override fun getItem(position: Int) = filteredTasks[position]
    override fun getItemId(position: Int) = filteredTasks[position].id.hashCode().toLong()
    override fun getCount() = filteredTasks.size
}