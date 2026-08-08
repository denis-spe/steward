package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.core.text.isDigitsOnly

class CustomInputTransformation : InputTransformation {
    override fun TextFieldBuffer.transformInput() {
        if (!asCharSequence().isDigitsOnly() && !asCharSequence().contains(".")) {
            revertAllChanges()
        }

//        placeCursorBeforeCharAt(length)
    }
}