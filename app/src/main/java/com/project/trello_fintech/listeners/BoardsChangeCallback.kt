package com.project.trello_fintech.listeners

import androidx.recyclerview.widget.DiffUtil
import com.project.trello_fintech.models.IListItem

/**
 * DiffUtil.Callback для отслеживания изменений в списке досок и категорий
 * @property before List<IListItem>
 * @property after List<IListItem>
 */
class BoardsChangeCallback(
        private val before: List<IListItem>,
        private val after: List<IListItem>
    ): DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return before[oldItemPosition] === after[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return before[oldItemPosition] == after[newItemPosition]
    }

    override fun getNewListSize() = after.size
    override fun getOldListSize() = before.size
}