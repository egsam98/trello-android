package com.project.trello_fintech.adapters

import androidx.recyclerview.widget.RecyclerView
import io.reactivex.subjects.PublishSubject


/**
 * Адаптер, наделенный способностью к поиску элементов по тексту
 * @param VH - ViewHolder
 * @property onSearchFinish PublishSubject<Int>
 */
interface ISearchableAdapter<VH: RecyclerView.ViewHolder> {
    fun search(text: String)
    fun onMatch(holder: VH)
    fun onMismatch(holder: VH)

    val onSearchFinish: PublishSubject<Int>
}