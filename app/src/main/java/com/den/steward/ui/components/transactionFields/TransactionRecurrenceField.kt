// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.R
import com.den.steward.backend.dataStructure.RecurrencePattern
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.formattedDate
import com.den.steward.helper.formattedTime
import com.den.steward.ui.components.DateDialog
import com.den.steward.ui.components.TimeDialog
import java.time.LocalDateTime

@Composable
fun TransactionRecurrenceField(
    colorResId: Int,
    startedAt: MutableState<LocalDateTime>,
    endAt: MutableState<LocalDateTime>,
    recurrence: MutableState<RecurrencePattern>
) {
    val onDialogShow = remember { mutableStateOf(false) }
    val color = colorResource(id = colorResId)

    if (onDialogShow.value) {
        RecurrenceDialog(
            color = color,
            startedAt = startedAt,
            endAt = endAt,
            recurrence = recurrence,
            onDismissRequest = { onDialogShow.value = false }
        )
    }

    TransactionRecurrenceFieldItem(
        onDialogShow = onDialogShow,
        startedAt = startedAt,
        endAt = endAt,
        recurrence = recurrence
    )
}

@Composable
private fun TransactionRecurrenceFieldItem(
    onDialogShow: MutableState<Boolean>,
    startedAt: MutableState<LocalDateTime>,
    endAt: MutableState<LocalDateTime>,
    recurrence: MutableState<RecurrencePattern>
) {
    TransactionFieldCard(
        title = "Recurrence",
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.repeat),
                contentDescription = "Recurrence",
                modifier = Modifier.size(ICON_SIZE)
            )
        },

        headlineContent = {
            if (endAt.value > startedAt.value) {
                Column (
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        startedAt.value.formatedDateTime,
                        style = MaterialTheme.typography.labelSmall
                    )

                    Text(
                        endAt.value.formatedDateTime,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        trailingContent = {
            Text(
                when (recurrence.value) {
                    is RecurrencePattern.NONE -> "Not Repeatable"
                    is RecurrencePattern.DAILY -> "Daily"
                    is RecurrencePattern.WEEKLY -> "Weekly"
                    is RecurrencePattern.MONTHLY -> "Monthly"
                    is RecurrencePattern.YEARLY -> "Yearly"
                    is RecurrencePattern.Custom -> "Schedule"
                },
                style = MaterialTheme.typography.labelMedium
            )
        }
    ) {
        onDialogShow.value = true
    }
}

@Composable
private fun RecurrenceDialog(
    color: Color,
    startedAt: MutableState<LocalDateTime>,
    endAt: MutableState<LocalDateTime>,
    recurrence: MutableState<RecurrencePattern>,
    onDismissRequest: () -> Unit
) {
    Dialog(
        onDismissRequest =  onDismissRequest
    ) {
        Card {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Recurrence Header
                RecurrenceHeaderContent()

                // Recurrence Content
                RecurrenceContent(
                    color = color,
                    startedAt = startedAt,
                    endAt = endAt,
                    recurrence = recurrence,
                    onDismissRequest = onDismissRequest
                )
            }
        }
    }
}

@Composable
private fun RecurrenceHeaderContent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "Recurrence",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            "Set the recurrence pattern for this transaction and schedule it",
            style = MaterialTheme.typography.labelSmall,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        HorizontalDivider(
            modifier = Modifier.padding(vertical = 8.dp),
            color = Color.Gray
        )
    }
}

@Composable
private fun RecurrenceRow(
    content: @Composable (RowScope.() -> Unit)
) {
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(50.dp),
        verticalAlignment = Alignment.CenterVertically,
        content = content
    )
}

@Composable
private fun RecurrenceContent(
    color: Color,
    startedAt: MutableState<LocalDateTime>,
    endAt: MutableState<LocalDateTime>,
    recurrence: MutableState<RecurrencePattern>,
    onDismissRequest: () -> Unit
) {
    val startedAtDate = remember { mutableStateOf(startedAt.value.toLocalDate() ) }
    val startedAtTime = remember { mutableStateOf(startedAt.value.toLocalTime()) }

    val endAtDate = remember { mutableStateOf(endAt.value.toLocalDate()) }
    val endAtTime = remember { mutableStateOf(endAt.value.toLocalTime()) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val showRecurrencePicker = remember { mutableStateOf("") }
    val transactionFieldState = remember { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    RecurrenceRow {
        RecurrenceDateContent(
            title = "Start At",
            color = color,
            dateText = startedAtDate.value.formattedDate,
            timeText = startedAtTime.value.formattedTime,
            onDateClick = {
                showDatePicker.value = true
                showRecurrencePicker.value = "Starting Date"
            },
            onTimeClick = {
                showTimePicker.value = true
                showRecurrencePicker.value = "Starting Time"
            }
        )


        RecurrenceDateContent(
            title = "End At",
            color = color,
            dateText = endAtDate.value.formattedDate,
            timeText = endAtTime.value.formattedTime,
            onDateClick = {
                showDatePicker.value = true
                showRecurrencePicker.value = "Deadline Date"
            },
            onTimeClick = {
                showTimePicker.value = true
                showRecurrencePicker.value = "Deadline Time"
            }
        )
    }

    if (showDatePicker.value) {
        DateDialog(
            title = "recurrence",
            color = color,
            headerTitle = showRecurrencePicker.value,
            showDatePicker = showDatePicker,
            localDateState = if (showRecurrencePicker.value == "Starting Date") startedAtDate else endAtDate
        )
    }

    RecurrenceBottomButton(
        color = color,
        transactionFieldState = transactionFieldState,
        onConfirm = {
            val newStart = startedAtDate.value.atTime(startedAtTime.value)
            val newEnd = endAtDate.value.atTime(endAtTime.value)

            when {
                newEnd <= newStart -> {
                    transactionFieldState.value = TransactionFieldState.Error("End date must be after start date")
                }
                // Allow a 1-minute grace period for "now"
                newStart < LocalDateTime.now().minusMinutes(1) -> {
                    transactionFieldState.value = TransactionFieldState.Error("Start date cannot be in the past")
                }
                newEnd > LocalDateTime.now().plusYears(1) -> {
                    transactionFieldState.value = TransactionFieldState.Error("End date must be within 1 year")
                }
                else -> {
                    startedAt.value = newStart
                    endAt.value = newEnd
                    transactionFieldState.value = TransactionFieldState.Success
                    onDismissRequest()
                }
            }


            if (transactionFieldState.value is TransactionFieldState.Success) {
                onDismissRequest()
            }
        },
        onCancel = {
            onDismissRequest()

            showDatePicker.value = false
            showTimePicker.value = false

            startedAt.value = LocalDateTime.now()
            endAt.value = LocalDateTime.now()
        }
    )

    AnimatedVisibility(
        visible = transactionFieldState.value is TransactionFieldState.Error
    ) {
        RecurrenceErrorMessage(
            message = (transactionFieldState.value as TransactionFieldState.Error).message
        )
    }

    if (showTimePicker.value) {
        TimeDialog(
            title = "recurrence",
            color = color,
            headerTitle = showRecurrencePicker.value,
            showTimePicker = showTimePicker,
            localTimeState = if (showRecurrencePicker.value == "Starting Time") startedAtTime else endAtTime
        )
    }

}

@Composable
private fun RecurrenceDateContent(
    title: String,
    dateText: String,
    timeText: String,
    color: Color,
    onDateClick: () -> Unit = {},
    onTimeClick: () -> Unit = {}
) {
    Surface(
        tonalElevation = 4.dp,
        shadowElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                title,
                style = MaterialTheme.typography.labelMedium
            )

            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                   primary = color
                )
            ) {
                TextButton(
                    onClick = onDateClick,
                ) {
                    Text(
                        dateText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                TextButton(
                    onClick = onTimeClick,
                ) {
                    Text(
                        timeText,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    }
}

@Composable
private fun RecurrenceErrorMessage(
    message: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            message,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun RecurrenceBottomButton(
    color: Color,
    onConfirm: () -> Unit,
    onCancel: () -> Unit,
    transactionFieldState: MutableState<TransactionFieldState>,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = onCancel,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.textButtonColors().copy(
                contentColor = MaterialTheme.colorScheme.onBackground
            )
        ) {
            Text("Cancel")
        }

        Button (
            onClick = onConfirm,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = if (transactionFieldState.value is TransactionFieldState.Error)
                    MaterialTheme.colorScheme.error else color
            )
        ) {
            Text(
                "Confirm",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White
            )
        }
    }
}
