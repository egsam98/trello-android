package com.project.trello_fintech.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.models.Checklist
import com.project.trello_fintech.view_models.TaskDetailViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Диалоговое окно создания/редактирования элемента чек-листа
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property taskDetailViewModel TaskDetailViewModel
 * @property dialogText TextInputEditText
 */
class CheckitemDialogFragment: DialogFragment() {
    companion object {
        private const val CHECKLIST_ID_ARG = "checklist_id"
        private const val CHECKITEM_ID_ARG = "checkitem_id"
        private const val CHECKITEM_TITLE_ARG = "checkitem_title"
        fun create(checklist: Checklist, checkitem: Checklist.Item?): CheckitemDialogFragment {
            val bundle = Bundle().apply {
                putString(CHECKLIST_ID_ARG, checklist.id)
                checkitem?.let {
                    putString(CHECKITEM_ID_ARG, it.id)
                    putString(CHECKITEM_TITLE_ARG, it.title)
                }
            }
            return CheckitemDialogFragment().apply { arguments = bundle }
        }
    }

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val taskDetailViewModel by lazy {
        cleanableViewModelProvider.get<TaskDetailViewModel>(requireParentFragment().viewLifecycleOwner)
    }

    lateinit var dialogText: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)
        dialogText = TextInputEditText(context).apply {
            val text = savedInstanceState?.getCharSequence("text")?: arguments?.getString(CHECKITEM_TITLE_ARG)
            setText(text)
        }
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.checkitem)
            .setView(dialogText)
            .setPositiveButton(R.string.save) { _,_ ->
                val checklistId = arguments?.getString(CHECKLIST_ID_ARG)
                val checkitemId = arguments?.getString(CHECKITEM_ID_ARG)

                val newTitle = dialogText.text.toString()
                checklistId?.let {
                    checkitemId?.let {
                        taskDetailViewModel.updateCheckitem(it, newTitle)
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
        outState.putCharSequence("text", dialogText.text)
    }
}