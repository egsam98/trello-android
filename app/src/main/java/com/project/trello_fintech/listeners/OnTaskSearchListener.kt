package com.project.trello_fintech.listeners

import android.widget.SearchView
import android.widget.Toast
import androidx.lifecycle.LifecycleObserver
import com.project.trello_fintech.adapters.ISearchableAdapter
import com.woxthebox.draglistview.BoardView


/**
 * Поиск задачи среди задач в BoardView по введенному тексту в SearchView
 * @property boardView BoardView
 */
class OnTaskSearchListener(private val boardView: BoardView): SearchView.OnQueryTextListener, LifecycleObserver {
    override fun onQueryTextChange(p0: String?) = true

    override fun onQueryTextSubmit(text: String): Boolean {
        var countsNum = 0
        var sumCount = 0
        (0 until boardView.columnCount)
            .map { columnInd ->
                val adapter = boardView.getAdapter(columnInd) as ISearchableAdapter<*>
                adapter.search(text)
                adapter.onSearchFinish
            }
            .forEach { subject ->
                subject.subscribe {
                    sumCount += it
                    countsNum++
                    if (countsNum == boardView.columnCount)
                        finish(sumCount)
                }
            }
        return true
    }

    private fun finish(sumCount: Int) {
        val resultMsg = when (sumCount) {
            0 -> "По запросу ничего не найдено"
            else -> "Найдено $sumCount задач"
        }
        Toast.makeText(boardView.context, resultMsg, Toast.LENGTH_SHORT).show()
    }
}