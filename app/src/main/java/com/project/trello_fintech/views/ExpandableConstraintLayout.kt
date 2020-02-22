package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import androidx.constraintlayout.widget.ConstraintLayout
import com.project.trello_fintech.utils.SetValueOnceDelegate


/**
 * ConstraintLayout, занимающий весь экран по клику
 * @property state State
 */
class ExpandableConstraintLayout(cxt: Context, attributeSet: AttributeSet): ConstraintLayout(cxt, attributeSet) {

    private inner class State {
        var isExpanded = false
            set(value) {
                field = value
                when (value) {
                    true -> setLayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, 0)
                    false -> setLayoutParams(state.width, state.height, state.padding)
                }
            }
        var width by SetValueOnceDelegate(0)
        var height by SetValueOnceDelegate(0)
        var padding by SetValueOnceDelegate(0)
    }

    private var state = State()

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)
        state.height = height
        state.width = width
        state.padding = paddingLeft
    }

    fun expandOrRestore() {
        state.isExpanded = !state.isExpanded
    }

    private fun setLayoutParams(width: Int, height: Int, padding: Int) {
        val changedLayoutParams = layoutParams.apply {
            this.width = width
            this.height = height
        }
        layoutParams = changedLayoutParams
        setPadding(padding, padding, padding, padding)
    }
}