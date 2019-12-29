package com.project.trello_fintech.listeners

import android.content.Context
import android.widget.Filter
import android.widget.SearchView
import android.widget.Toast
import io.reactivex.Single
import io.reactivex.rxkotlin.toObservable


/**
 * Поиск задачи среди задач в BoardView по введенному тексту в SearchView
 * @property cxt Context
 * @property filters List<Filter>
 */
class OnTaskSearchListener(
        private val cxt: Context,
        private val filters: List<Filter>
    ): SearchView.OnQueryTextListener {

    override fun onQueryTextChange(text: String): Boolean = true

    override fun onQueryTextSubmit(searchText: String): Boolean {
        filters.toObservable()
            .flatMapSingle {
                Single.create<Int> { emitter ->
                    it.filter(searchText) { emitter.onSuccess(it) }
                }
            }
            .reduce { t1: Int, t2: Int -> t1 + t2 }
            .doOnSuccess { Toast.makeText(cxt, "Найдено $it задач(-и)", Toast.LENGTH_SHORT).show() }
            .subscribe()
        return true
    }
}