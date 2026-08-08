package com.den.steward.ui.components

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.delete
import androidx.compose.foundation.text.input.insert
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.den.steward.helper.eval
import com.den.steward.helper.formatResult

@Composable
fun CustomAmountKeyBoard(
    modifier: Modifier = Modifier,
    state: TextFieldState,
    focusRequester: FocusRequester? = null,
    contentColor: Color = Color.Unspecified,
    visible: Boolean = false,
    onDone: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    val density = LocalDensity.current

    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { with(density) { 400.dp.roundToPx() } } + expandVertically(
            expandFrom = Alignment.Bottom
        ) + fadeIn(),
        exit = slideOutVertically { with(density) { 400.dp.roundToPx() } } + shrinkVertically(
            shrinkTowards = Alignment.Bottom
        ) + fadeOut(),
        modifier = modifier
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val keys = listOf(
                    listOf("C", "÷", "×", "DEL"),
                    listOf("1", "2", "3", "+"),
                    listOf("4", "5", "6", "%"),
                    listOf("7", "8", "9", "-"),
                    listOf(".", "0", "=")
                )

                keys.forEach { row ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        row.forEach { key ->
                            KeyButton(
                                text = key,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    when (key) {
                                        "DEL" -> {
                                            if (state.text.isNotEmpty()) {
                                                state.edit {
                                                    // Delete the last character if no selection, otherwise delete selection
                                                    if (selection.collapsed) {
                                                        if (selection.start > 0) {
                                                            delete(
                                                                selection.start - 1,
                                                                selection.start
                                                            )
                                                        }
                                                    } else {
                                                        delete(selection.start, selection.end)
                                                    }
                                                }
                                            }
                                        }


                                        "0" -> {
                                            state.edit {
                                                val text = originalText

                                                val operators = charArrayOf('+', '-', '*', '/', '%')
                                                val lastOperatorIndex =
                                                    text.lastIndexOfAny(operators)

                                                val currentNumber =
                                                    text.substring(lastOperatorIndex + 1)

                                                when {
                                                    // Case: whole input is "0" → do nothing
                                                    text == "0" -> return@edit

                                                    // Case: current number is "0" (e.g., "5+0") → keep single zero
                                                    currentNumber == "0" -> return@edit

                                                    else -> {
                                                        replace(selection.start, selection.end, "0")
                                                    }
                                                }
                                            }
                                        }

                                        "C" -> {
                                            state.clearText()
                                        }

                                        "=" -> {
                                            state.edit {
                                                delete(selection.start, selection.end)
                                                val result = originalText.eval.formatResult
                                                Log.d("CustomAmountKeyBoard", "result: $result")
                                                replace(0, length, result)
                                            }
                                        }

                                        in listOf("+", "×", "÷", "%", "-") -> {
                                            state.edit {
                                                val text = originalText

                                                if (text.isEmpty() && key == "-") {
                                                    insert(0, "-")
                                                    return@edit
                                                }

                                                if (text.isEmpty()) return@edit

                                                val operators = charArrayOf('+', '-', '×', '÷', '%')
                                                val lastChar = text.last()

                                                when {
                                                    // If last char is already an operator → replace it instead of adding
                                                    lastChar in operators -> {
                                                        delete(length - 1, length)
                                                        insert(length, key)
                                                    }

                                                    else -> {
                                                        replace(selection.start, selection.end, key)
                                                    }
                                                }
                                            }
                                        }

                                        else -> {
                                            state.edit {
                                                val text = originalText

                                                // Find last operator
                                                val lastOperatorIndex = text.lastIndexOfAny(
                                                    charArrayOf('+', '-', '×', '÷', '%')
                                                )

                                                // Get current number being typed
                                                val currentNumber =
                                                    text.substring(lastOperatorIndex + 1)

                                                if (currentNumber.startsWith("0")) {
                                                    delete(selection.start - 1, selection.start)
                                                }

                                                if (currentNumber.length < 13) {
                                                    replace(selection.start, selection.end, key)
                                                }
                                            }
                                        }
                                    }
                                    focusRequester?.requestFocus()
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    KeyButton(
                        text = "CANCEL",
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.Unspecified,
                        contentColor = contentColor,
                        onClick = onCancel
                    )

                    Text(
                        "|",
                        color = contentColor,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )

                    KeyButton(
                        text = "DONE",
                        modifier = Modifier.weight(1f),
                        backgroundColor = Color.Unspecified,
                        contentColor = contentColor,
                        onClick = onDone
                    )

                }
            }
        }
    }
}

@Composable
private fun KeyButton(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    contentColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(backgroundColor)
            .focusProperties { canFocus = false }
            .clickable { onClick() }
            .padding(vertical = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        when (text) {
            "DEL" -> {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = "Delete",
                    tint = contentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            "DONE" -> {
                Row(horizontalArrangement = Arrangement.Start) {
                    Row {
                        Icon(Icons.Default.Check, contentDescription = null)
                        Text(text = " Done", color = contentColor, fontWeight = FontWeight.Bold)
                    }
                }
            }

            "CANCEL" -> {
                Row(horizontalArrangement = Arrangement.End) {
                    Row {
                        Icon(Icons.Default.Cancel, contentDescription = null)
                        Text(text = " Cancel", color = contentColor, fontWeight = FontWeight.Bold)
                    }
                }
            }


            else -> {
                Text(
                    text = text,
                    color = contentColor,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}