package com.project.trello_fintech.fragments

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
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
        fun create(checklist: Checklist?): ChecklistDialogFragment {
            return ChecklistDialogFragment().apply {
                checklist?.let {
                    val bundle = Bundle().apply {
                        putString(CHECKLIST_ID_ARG, it.id)
                        putString(CHECKLIST_TITLE_ARG, it.title)
                    }
                    arguments = bundle
                }
            }
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        dialogText = TextInputEditText(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            val text = savedInstanceState?.getCharSequence("text")?: arguments?.getString(CHECKLIST_TITLE_ARG)
            setText(text)
        }
        val wrapper = LinearLayout(context).apply {
            setPadding(20, 0, 20, 0)
            addView(dialogText)
        }
        val checklistId = arguments?.getString(CHECKLIST_ID_ARG)
        return AlertDialog.Builder(context)
            .setTitle(R.string.checklist_title)
            .setView(wrapper)
            .setPositiveButton(R.string.save) { _, _ ->
                val text = dialogText.text.toString()
                checklistId?.let {
                    taskDetailViewModel.updateChecklistTitle(it, text)
                }
                ?: run {
                    taskDetailViewModel.createChecklist(text)
                }
                dismiss()
            }
            .setNeutralButton(R.string.delete) { dialog, _ ->
                checklistId?.let {
                    taskDetailViewModel.deleteChecklist(it)
                }
                dialog.dismiss()
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("text", dialogText.text)
    }
}