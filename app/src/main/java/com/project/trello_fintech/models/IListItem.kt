package com.project.trello_fintech.models

import com.project.trello_fintech.models.IListItem.Companion.NOTHING


/**
 * Класс элемента списка
 */
interface IListItem {
    companion object {
        const val NOTHING = -1
        const val HEADER = 0
        const val BODY = 1
    }

    fun getType(): Int
}

/**
 * Пустой список (заглушка)
 */
object NothingListItem: IListItem {
    override fun getType() = NOTHING
}