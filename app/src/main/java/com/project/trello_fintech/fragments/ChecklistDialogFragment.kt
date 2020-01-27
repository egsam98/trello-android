package com.project.trello_fintech.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.R


/**
 * Диалоговое окно редактирования названия чек-листа
 */
class ChecklistDialogFragment: DialogFragment() {

    companion object {
        private const val CHECKLIST_TITLE_ARG = "checklist_title"
        fun create(checklistTitle: String): ChecklistDialogFragment {
            val bundle = Bundle().apply { putString(CHECKLIST_TITLE_ARG, checklistTitle) }
            return ChecklistDialogFragment().apply { arguments = bundle }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = TextInputEditText(context).apply {
            setText(arguments?.getString(CHECKLIST_TITLE_ARG))
        }
        return AlertDialog.Builder(context)
            .setTitle(R.string.checklist_title)
            .setView(dialogView)
            .setPositiveButton(R.string.save) { _, _ ->
                dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }
}