package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.ActionMenuView
import androidx.cardview.widget.CardView
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import javax.inject.Inject


private const val LAYOUT = R.layout.task_detail_section

/**
 * Раздел страницы детальной информации о выбранной задаче
 * @property textView TextView
 */
class TaskDetailSectionView(cxt: Context, attrs: AttributeSet): CardView(cxt, attrs) {
    @Inject
    lateinit var mainActivity: MainActivity

    private val textView: TextView
    val menuView: ActionMenuView by lazy { findViewById<ActionMenuView>(R.id.actions_bar) }

    init {
        MainActivity.component.inject(this)

        View.inflate(context, LAYOUT, this)
        val typedArr = cxt.obtainStyledAttributes(attrs, R.styleable.TaskDetailSectionView, 0, 0)
        findViewById<ImageView>(R.id.icon).setImageDrawable(typedArr.getDrawable(R.styleable.TaskDetailSectionView_iconSrc))
        textView = findViewById<TextView>(R.id.text).apply {
            text = typedArr.getText(R.styleable.TaskDetailSectionView_text)?.toString().orEmpty()
        }
        typedArr.recycle()
    }

    fun setText(text: String) {
        textView.text = text
    }

    fun inflateMenu(res: Int) {
        mainActivity.menuInflater.inflate(res, menuView.menu)
    }
}