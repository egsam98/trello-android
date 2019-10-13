package com.project.homework_2.presenters

/**
 * Интерфейс презентора для работы со списками
 * @param T
 */
interface IListPresenter<T> {
    fun add(obj: T)
    fun removeAt(pos: Int)
}