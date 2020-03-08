package com.project.trello_fintech.fragments.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import javax.inject.Inject
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.models.Column
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TasksViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import java.util.*


/**
 * Диалогове окно создания новой задачи
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property viewModel TasksViewModel
 * @property dialogText EditText
 */
class AddTaskDialogFragment: DialogFragment() {
    companion object {
        private const val COLUMN_ARG = "column"
        private const val TEXT_STATE = "text"
        private const val DATETIME_STATE = "datetime"

        fun create(column: Column): AddTaskDialogFragment {
            val bundle = Bundle().apply { putSerializable(COLUMN_ARG, column) }
            return AddTaskDialogFragment()
                .apply { arguments = bundle }
        }
    }

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val viewModel by lazy {
        cleanableViewModelProvider.get<TasksViewModel>(requireParentFragment().viewLifecycleOwner)
    }

    private lateinit var dialogText: EditText
    private lateinit var dueDatePicker: DatePicker
    private lateinit var dueTimePicker: TimePicker

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_task_dialog, null, false)

        dialogText = dialogView.findViewById<TextInputEditText>(R.id.text).apply {
            setText(savedInstanceState?.getString(TEXT_STATE))
        }
        val date = savedInstanceState?.getSerializable(DATETIME_STATE) as? Date
        dueDatePicker = dialogView.findViewById<DatePicker>(R.id.due_date_picker).apply {
            date?.let { updateDate(it.year, it.month, it.day) }
        }
        dueTimePicker = dialogView.findViewById<TimePicker>(R.id.due_time_picker).apply {
            date?.let {
                hour = it.hours
                minute = it.minutes
            }
        }

        val column = requireArguments().getSerializable(COLUMN_ARG) as Column
        return AlertDialog.Builder(requireContext())
            .setCustomTitle(createCustomTitleView(column.title))
            .setView(dialogView)
            .setPositiveButton(R.string.create) { _, _ ->
                val task = Task(dialogText.text.toString()).apply {
                    dueDate = createDate()
                }
                viewModel.add(column, task)
                dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(TEXT_STATE, dialogText.text.toString())
        outState.putSerializable(DATETIME_STATE, createDate())
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

    private fun createDate(): Date = with(Calendar.getInstance()) {
        set(dueDatePicker.year, dueDatePicker.month, dueDatePicker.dayOfMonth, dueTimePicker.hour, dueTimePicker.minute)
        time
    }
}