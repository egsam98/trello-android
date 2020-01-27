package com.project.trello_fintech.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Диалоговое окно редактирования названия чек-листа
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property taskDetailViewModel TaskDetailViewModel
 * @property dialogText TextInputEditText
 */
class ChecklistDialogFragment: DialogFragment() {

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(requireParentFragment().viewLifecycleOwner)
    }

    lateinit var dialogText: TextInputEditText

    companion object {
        private const val CHECKLIST_ID_ARG = "checklist_id"
        private const val CHECKLIST_TITLE_ARG = "checklist_title"
        fun create(checklist: Checklist): ChecklistDialogFragment {
            val bundle = Bundle().apply {
                putString(CHECKLIST_ID_ARG, checklist.id)
                putString(CHECKLIST_TITLE_ARG, checklist.title)
            }
            return ChecklistDialogFragment().apply { arguments = bundle }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        dialogText = TextInputEditText(context).apply {
            val text = savedInstanceState?.getCharSequence("text")?: arguments?.getString(CHECKLIST_TITLE_ARG)
            setText(text)
        }
        return AlertDialog.Builder(context)
            .setTitle(R.string.checklist_title)
            .setView(dialogText)
            .setPositiveButton(R.string.save) { _, _ ->
                arguments?.getString(CHECKLIST_ID_ARG)?.let {
                    taskDetailViewModel.updateChecklistTitle(it, dialogText.text.toString())
                }
                dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("text", dialogText.text)
    }
}