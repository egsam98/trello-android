package com.project.trello_fintech.views

import android.content.Context
import android.widget.ImageView
import android.widget.SearchView


/**
 * SearchView с функцией обратного вызова-обработчика нажатия кнопки закрытия поиска
 * @property onClear Function0<Unit>?
 */
class ClearableSearchView(cxt: Context?): SearchView(cxt) {
    var onClear: (() -> Unit)? = null

    init {
        val searchCloseButtonId: Int = resources.getIdentifier("android:id/search_close_btn", null, null)
        findViewById<ImageView>(searchCloseButtonId).setOnClickListener {
            onClear?.invoke()
        }
    }
}