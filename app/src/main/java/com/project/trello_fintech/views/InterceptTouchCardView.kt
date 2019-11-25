package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.cardview.widget.CardView


class InterceptTouchCardView(cxt: Context, attributeSet: AttributeSet): CardView(cxt, attributeSet) {
    override fun onInterceptTouchEvent(ev: MotionEvent?) = true
}