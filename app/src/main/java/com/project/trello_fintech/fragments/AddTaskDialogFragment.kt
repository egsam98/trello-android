package com.project.trello_fintech.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider


/**
 * Диалогове окно создания новой задачи
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property viewModel TasksViewModel
 * @property dialogText EditText
 */
class AddTaskDialogFragment: DialogFragment() {

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val viewModel by lazy {
        cleanableViewModelProvider.get<TasksViewModel>(requireParentFragment())
    }

    private lateinit var dialogText: EditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        dialogText = TextInputEditText(requireContext()).apply {
            setHint(R.string.add_task_text_hint)
            setText(savedInstanceState?.getCharSequence("text"))
            setTextColor(Color.BLACK)
        }
        val column = arguments?.getSerializable("column") as Column
        return AlertDialog.Builder(requireContext())
            .setCustomTitle(createCustomTitleView(column.title))
            .setView(dialogText)
            .setPositiveButton("Создать") { _, _ ->
                viewModel.add(column, Task(dialogText.text.toString()))
                dismiss()
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("text", dialogText.text)
    }

    private fun createCustomTitleView(columnTitle: String): View {
        return LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(15, 5, 15, 5)
            addView(TextView(requireContext()).apply {
                setText(R.string.add_task_text)
                textSize = 25f
                typeface = Typeface.DEFAULT_BOLD
                setTextColor(Color.BLACK)
            })
            addView(TextView(requireContext()).apply {
                text = Html.fromHtml("в разделе <b><font color='black'>$columnTitle</font></b>")
                textSize = 15f
            })
        }
    }
}