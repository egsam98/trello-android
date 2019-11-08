package com.project.trello_fintech.listeners

import androidx.recyclerview.widget.DiffUtil
import com.project.trello_fintech.models.IListItem

/**
 * DiffUtil.Callback для отслеживания изменений в списке досок и категорий
 */
object BoardsChangeCallback: DiffUtil.ItemCallback<IListItem>() {

    override fun areItemsTheSame(oldItem: IListItem, newItem: IListItem): Boolean {
        return oldItem === newItem
    }

    override fun areContentsTheSame(oldItem: IListItem, newItem: IListItem): Boolean {
        return oldItem == newItem
    }
}