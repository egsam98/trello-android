package com.project.homework_2.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.homework_2.R
import com.project.homework_2.presenters.BoardsPresenter


/**
 * Диалоговое окно создания новой доски
 * @property textInput TextInputEditText
 */
class AddBoardDialogFragment: DialogFragment() {

    private lateinit var textInput: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        textInput = TextInputEditText(context).apply {
            hint = resources.getString(R.string.add_board_title_hint)
            setText(savedInstanceState?.getCharSequence("input"))
        }

        return with(AlertDialog.Builder(context!!)) {
            setTitle(R.string.add_board_title)
            setView(textInput)
            setPositiveButton("Создать") { _,_ ->
                BoardsPresenter.addNew(textInput.text.toString())
                dismiss()
            }
            setNegativeButton("Отмена", null)
            create()
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("input", textInput.text)
    }
}