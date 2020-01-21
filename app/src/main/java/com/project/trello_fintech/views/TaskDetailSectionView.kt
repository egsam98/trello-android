package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.project.trello_fintech.R


private const val LAYOUT = R.layout.task_detail_section

/**
 * Раздел страницы детальной информации о выбранной задаче
 */
class TaskDetailSectionView(cxt: Context, attrs: AttributeSet): CardView(cxt, attrs) {
    var text: String = ""
        set(value) {
            field = value
            textView?.text = value
        }

    private val textView: TextView?

    init {
        View.inflate(context, LAYOUT, this)
        val typedArr = cxt.obtainStyledAttributes(attrs, R.styleable.TaskDetailSectionView, 0, 0)
        findViewById<ImageView>(R.id.icon).setImageDrawable(typedArr.getDrawable(R.styleable.TaskDetailSectionView_iconSrc))
        text = typedArr.getText(R.styleable.TaskDetailSectionView_text)?.toString().orEmpty()
        textView = findViewById<TextView>(R.id.text).apply { this.text = text }
        typedArr.recycle()
    }
}