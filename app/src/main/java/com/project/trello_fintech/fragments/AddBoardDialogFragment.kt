package com.project.trello_fintech.fragments

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.project.trello_fintech.R
import com.project.trello_fintech.activities.MainActivity
import com.project.trello_fintech.models.Board
import com.project.trello_fintech.view_models.BoardsViewModel
import com.project.trello_fintech.view_models.utils.CleanableViewModelProvider
import javax.inject.Inject


/**
 * Диалоговое окно создания новой доски
 * @property textInput TextInputEditText
 * @property categoriesSpinner Spinner
 * @property cleanableViewModelProvider CleanableViewModelProvider
 * @property viewModel BoardsViewModel
 */
class AddBoardDialogFragment: DialogFragment() {

    private lateinit var textInput: TextInputEditText
    private lateinit var categoriesSpinner: Spinner

    @Inject
    lateinit var cleanableViewModelProvider: CleanableViewModelProvider

    private val viewModel by lazy {
        cleanableViewModelProvider.get<BoardsViewModel>(requireParentFragment())
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        MainActivity.component.inject(this)

        textInput = TextInputEditText(context).apply {
            hint = resources.getString(R.string.add_board_title_hint)
            setText(savedInstanceState?.getCharSequence("input"))
        }

        val dialogView = LayoutInflater.from(context).inflate(R.layout.fragment_add_board_dialog, null).apply {
            textInput = findViewById<TextInputEditText>(R.id.add_board_title).apply {
                setText (savedInstanceState?.getCharSequence("input"))
            }

            categoriesSpinner = findViewById<Spinner>(R.id.add_board_category).apply {
                viewModel.getAllCategories()
                    .subscribe {categories ->
                        adapter = ArrayAdapter<Board.Category>(context, android.R.layout.simple_spinner_item, categories)
                        savedInstanceState?.let { setSelection(it.getInt("category_index")) }
                    }
            }
        }

        return AlertDialog.Builder(context!!)
            .setTitle(R.string.add_board_title)
            .setView(dialogView)
            .setPositiveButton("Создать") { _,_ ->
                val newBoard = Board(textInput.text.toString(), categoriesSpinner.selectedItem as Board.Category)
                viewModel.add(newBoard)
                dismiss()
            }
            .setNegativeButton("Отмена", null)
            .create()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putCharSequence("input", textInput.text)
        outState.putInt("category_index", categoriesSpinner.selectedItemPosition)
    }
}