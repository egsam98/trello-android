package com.project.trello_fintech.views

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.ProgressBar
import android.graphics.drawable.GradientDrawable
import android.view.ViewGroup


/**
 * ProgressBar в виде круга
 */
class CircularProgressBar(cxt: Context, attrsSet: AttributeSet):
    ProgressBar(cxt, attrsSet, android.R.attr.progressBarStyleLarge) {

    init {
        visibility = View.GONE
        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        background = GradientDrawable().apply {
            shape = GradientDrawable.RING
            cornerRadii = floatArrayOf(8f, 8f, 8f, 8f, 0f, 0f, 0f, 0f)
            setColor(Color.RED)
        }
    }

    fun loading() { visibility = View.VISIBLE }
    fun done() { visibility = View.GONE }
}