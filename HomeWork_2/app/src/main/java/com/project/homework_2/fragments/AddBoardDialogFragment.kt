package com.project.homework_2.fragments

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.design.widget.TextInputEditText
import android.support.v7.app.AlertDialog
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
        val view = TextInputEditText(context)
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