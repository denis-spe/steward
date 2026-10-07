package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer

class CustomInputTransformation : InputTransformation {
    private val allowedChars = setOf(
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
        '.', ',', '+', '-', '×', '÷', '%', '*', '/', ' ',
    )

    override fun TextFieldBuffer.transformInput() {
        val text = asCharSequence()
        if (text.isNotEmpty() && text.any { it !in allowedChars }) {
            revertAllChanges()
        }
    }
}
