package com.project.trello_fintech.utils

import android.widget.Filter
import android.widget.Filterable


interface ResettableFilterable: Filterable {
    override fun getFilter(): ResettableFilter
}

abstract class ResettableFilter: Filter() {
    abstract fun reset()
}