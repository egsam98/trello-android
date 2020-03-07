package com.project.trello_fintech.fragments.dialogs

import android.app.Dialog
import android.os.Bundle
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.adapters.AutoCompleteTasksAdapter
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.models.Task
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Диалоговое окно создания/редактирования элемента чек-листа
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property taskDetailViewModel TaskDetailViewModel
 * @property autoCompleteTextView TextInputEditText
 */
class CheckitemDialogFragment: DialogFragment() {
    companion object {
        private const val CHECKLIST_ID_ARG = "checklist_id"
        private const val CHECKITEM_ID_ARG = "checkitem_id"
        private const val CHECKITEM_TITLE_ARG = "checkitem_title"
        private const val TEXT_STATE = "text"
        fun create(checklist: Checklist, checkitem: Checklist.Item?): CheckitemDialogFragment {
            val bundle = Bundle().apply {
                putString(CHECKLIST_ID_ARG, checklist.id)
                checkitem?.let {
                    putString(CHECKITEM_ID_ARG, it.id)
                    putString(CHECKITEM_TITLE_ARG, it.title)
                }
            }
            return CheckitemDialogFragment()
                .apply { arguments = bundle }
        }
    }

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(requireParentFragment().viewLifecycleOwner)
    }

    private lateinit var autoCompleteTextView: AutoCompleteTextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        autoCompleteTextView = AutoCompleteTextView(context).apply {
            val text = savedInstanceState?.getCharSequence(TEXT_STATE)?: arguments?.getString(
                CHECKITEM_TITLE_ARG
            )
            setText(text)
        }

        taskDetailViewModel.findAllTasksInBoard {
            val adapter = AutoCompleteTasksAdapter(requireContext(), it)
            autoCompleteTextView.setAdapter(adapter)
            autoCompleteTextView.setOnItemClickListener { adapterView, _, i, _ ->
                val task = adapterView.getItemAtPosition(i) as Task
                autoCompleteTextView.setText(task.shortUrl)
            }
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.checkitem)
            .setView(autoCompleteTextView)
            .setPositiveButton(R.string.save) { _,_ ->
                val checklistId = arguments?.getString(CHECKLIST_ID_ARG)
                val checkitemId = arguments?.getString(CHECKITEM_ID_ARG)

                val newTitle = autoCompleteTextView.text.toString()
                checklistId?.let {
                    checkitemId?.let {
                        taskDetailViewModel.updateCheckitem(it, title = newTitle)
                    }?: run {
                        taskDetailViewModel.createCheckitem(checklistId, newTitle)
                    }
                }
                dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence(TEXT_STATE, autoCompleteTextView.text)
    }
}