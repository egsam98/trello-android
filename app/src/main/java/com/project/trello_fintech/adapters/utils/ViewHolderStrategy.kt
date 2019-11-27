package com.project.trello_fintech.adapters.utils

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

/**
 * В зависимости от ItemViewType используется определенный ViewHolder - реализуется стратегия
 */
interface ViewHolderStrategy<T> {
    fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder
    fun onBindViewHolder(holder: RecyclerView.ViewHolder, item: T)
}