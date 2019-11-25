package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ListView


/**
 * Выпадающий список
 * @property deltaY Float изменяемое значение оси ординат списка
 */
class DropDownListView(cxt: Context, attrsSet: AttributeSet): ListView(cxt, attrsSet) {

    private val deltaY: Float
        get() = height.toFloat()

    init {
        alpha = 0f
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        super.onLayout(changed, l, t, r, b)
        translationY = deltaY
    }

    fun openClose() {
        when (alpha) {
            1f -> close()
            0f -> open()
        }
    }

    private fun open() {
        animate()
            .translationYBy(-deltaY)
            .alpha(1f)
            .start()
    }

    private fun close() {
        animate()
            .translationYBy(deltaY)
            .alpha(0f)
            .start()
    }
}