package com.project.trello_fintech.views

import android.content.Context
import android.util.AttributeSet
import com.google.android.material.textfield.TextInputEditText

/**
 * TextInputEditText - нижнее подчеркивание (стиль) которого появляется только при начале редактирования
 */
class UnderlineOnClickEditText(cxt: Context, attributeSet: AttributeSet): TextInputEditText(cxt, attributeSet) {
    init {
        val originalDrawable = background
        background = null
        setOnFocusChangeListener { _, isFocused ->
            val drawable = when (isFocused) {
                true -> originalDrawable
                false -> null
            }
            background = drawable
        }
    }
}