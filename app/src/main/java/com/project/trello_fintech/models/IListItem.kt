package com.project.trello_fintech.models

import java.util.*


/**
 * Класс элемента списка
 * @property id Long
 */
abstract class IListItem {
    companion object {
        const val NOTHING = -1
        const val HEADER = 0
        const val BODY = 1
    }

    val id: Long = Calendar.getInstance().timeInMillis

    abstract fun getType(): Int
}

/**
 * Пустой список (заглушка)
 */
object NothingListItem: IListItem() {
    override fun getType() = NOTHING
}