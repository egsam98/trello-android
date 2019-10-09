package com.project.homework_2.fragments

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.homework_2.R
import com.project.homework_2.activities.MainActivity
import com.project.homework_2.presenters.BoardsPresenter


/**
 * Диалоговое окно создания новой доски
 * @property presenter BoardsPresenter?
 */
class AddBoardDialogFragment: DialogFragment() {

    private var presenter: BoardsPresenter? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val view = TextInputEditText(context).apply {
            hint = resources.getString(R.string.add_board_title_hint)
        }

        return with(AlertDialog.Builder(context!!)) {
            setTitle(R.string.add_board_title)
            setView(view)
            setPositiveButton("Создать") { _,_ ->
                presenter?.let {
                    it.addNew(view.text.toString())
                    dismiss()
                }
            }
            setNegativeButton("Отмена", null)
            create()
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        presenter = (activity as MainActivity).presenter
    }
}