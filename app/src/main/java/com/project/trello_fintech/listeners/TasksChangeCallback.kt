package com.project.trello_fintech.listeners

import androidx.recyclerview.widget.DiffUtil
import com.project.trello_fintech.models.Task

/**
 * @property before List<Task>
 * @property after List<Task>
 * @constructor
 */
class TasksChangeCallback(
        private val before: List<Task>,
        private val after: List<Task>
    ): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return before[oldItemPosition].id == after[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return before[oldItemPosition].text == after[newItemPosition].text
    }

    override fun getOldListSize() = before.size
    override fun getNewListSize() = after.size
}