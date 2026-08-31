// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItemColors
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.R
import com.den.steward.backend.entitles.RecurrencePattern
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.formattedDate
import com.den.steward.helper.formattedTime
import com.den.steward.ui.components.DateRangeDialog
import com.den.steward.ui.components.TimeDialog
import java.time.LocalDateTime

@Composable
fun TransactionRecurrenceField(
    colorResId: Int,
    startedAt: LocalDateTime,
    endAt: LocalDateTime,
    recurrence: RecurrencePattern,
    isStartNotEqualToEndDateTime: TransactionFieldState,
    colors: ListItemColors = ListItemDefaults.colors(),
    onStartTimeChange: (LocalDateTime) -> Unit,
    onEndTimeChange: (LocalDateTime) -> Unit,
    onRecurrenceChange: (RecurrencePattern) -> Unit,
    onIsStartNotEqualToEndDateTimeChange: (TransactionFieldState) -> Unit
) {
    val onDialogShow = remember { mutableStateOf(false) }
    val color = colorResource(id = colorResId)

    val selectedStartAt = remember { mutableStateOf<LocalDateTime?>(null) }
    val selectedEndAt = remember { mutableStateOf<LocalDateTime?>(null) }

    if (onDialogShow.value) {
        RecurrenceDialog(
            color = color,
            recurrence = recurrence,
            onRecurrenceChange = onRecurrenceChange,
            onDismissRequest = {
                onDialogShow.value = false
            }
        ) { finalStartDate, finalEndDate ->
            if (finalStartDate != null) {
                onStartTimeChange(finalStartDate)
                selectedStartAt.value = finalStartDate
            }
            if (finalEndDate != null) {
                onEndTimeChange(finalEndDate)
                selectedEndAt.value = finalEndDate
            }
        }
    }

    TransactionRecurrenceFieldItem(
        colors = if (isStartNotEqualToEndDateTime is TransactionFieldState.Error)
            colors.copy(
                containerColor = MaterialTheme.colorScheme.error
            ) else colors,
        onDialogShow = onDialogShow,
        startedAt = selectedStartAt,
        endAt = selectedEndAt,
        recurrence = recurrence,
        isStartNotEqualToEndDateTime = isStartNotEqualToEndDateTime,
        onIsStartNotEqualToEndDateTimeChange = onIsStartNotEqualToEndDateTimeChange
    )
}

@Composable
private fun TransactionRecurrenceFieldItem(
    modifier: Modifier = Modifier,
    onDialogShow: MutableState<Boolean>,
    startedAt: MutableState<LocalDateTime?>,
    endAt: MutableState<LocalDateTime?>,
    recurrence: RecurrencePattern,
    colors: ListItemColors,
    isStartNotEqualToEndDateTime: TransactionFieldState,
    onIsStartNotEqualToEndDateTimeChange: (TransactionFieldState) -> Unit,
) {

    LaunchedEffect(
        endAt.value
    ) {
        onIsStartNotEqualToEndDateTimeChange(TransactionFieldState.Initial)
    }

    TransactionFieldCard(
        title = "Recurrence",
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.repeat),
                contentDescription = "Recurrence",
                modifier = Modifier.size(ICON_SIZE)
            )
        },
        modifier = modifier,
        colors = colors,
        headlineContent = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (startedAt.value != null) {
                    Text(
                        text = "Starts: ${startedAt.value!!.formatedDateTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (endAt.value != null && startedAt.value != null && endAt.value!!.isAfter(startedAt.value!!)) {
                    Text(
                        text = "Ends: ${endAt.value!!.formatedDateTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            val text = if (isStartNotEqualToEndDateTime is TransactionFieldState.Error)
                    "Required"
                else when (recurrence) {
                is RecurrencePattern.NONE -> "Not Repeatable"
                is RecurrencePattern.DAILY -> "Daily"
                is RecurrencePattern.WEEKLY -> "Weekly"
                is RecurrencePattern.MONTHLY -> "Monthly"
                is RecurrencePattern.YEARLY -> "Yearly"
                is RecurrencePattern.Custom -> "Schedule"
            }
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = if (isStartNotEqualToEndDateTime is TransactionFieldState.Error)
                    Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    ) {
        onDialogShow.value = true
    }
}

@Composable
private fun RecurrenceDialog(
    color: Color,
    recurrence: RecurrencePattern,
    onRecurrenceChange: (RecurrencePattern) -> Unit,
    onDismissRequest: () -> Unit,
    onSubmit: (LocalDateTime?, LocalDateTime?) -> Unit,
) {
    Dialog(
        onDismissRequest =  onDismissRequest
    ) {
        Card {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.9f)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Recurrence Header
                RecurrenceHeaderContent()

                // Recurrence Content
                RecurrenceDialogContent(
                    color = color,
                    recurrence = recurrence,
                    onRecurrenceChange = onRecurrenceChange,
                    onDismissRequest = onDismissRequest,
                    onSubmit = onSubmit
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
private fun RecurrenceDialogContent(
    color: Color,
    recurrence: RecurrencePattern,
    onRecurrenceChange: (RecurrencePattern) -> Unit,
    onDismissRequest: () -> Unit,
    onSubmit: (finalStartDate: LocalDateTime?, finalEndDate: LocalDateTime?) -> Unit,
) {
    val nowState = remember { LocalDateTime.now() }
    val startedAtDate = remember { mutableStateOf(nowState.toLocalDate()) }
    val startedAtTime = remember { mutableStateOf(nowState.toLocalTime()) }

    val endAtDate = remember { mutableStateOf(nowState.toLocalDate()) }
    val endAtTime = remember { mutableStateOf(nowState.toLocalTime()) }

    val showDatePicker = remember { mutableStateOf(false) }
    val showTimePicker = remember { mutableStateOf(false) }
    val showRecurrencePicker = remember { mutableStateOf("") }
    val transactionFieldState = remember { mutableStateOf<TransactionFieldState>(TransactionFieldState.Initial) }

    // Select by date
    RecurrenceRow {

        RecurrenceDateContent(
            title = "Date Range",
            color = color,
            startDateText = startedAtDate.value.formattedDate,
            lastDateText = endAtDate.value.formattedDate,
            startTimeText = startedAtTime.value.formattedTime,
            lastTimeText = endAtTime.value.formattedTime,
            onDateClick = {
                showDatePicker.value = true
            },
            onStartTimeClick = {
                showTimePicker.value = true
                showRecurrencePicker.value = "Starting Time"
            },
            onLastTimeClick = {
                showTimePicker.value = true
                showRecurrencePicker.value = "Ending Time"
            }
        )
    }

    // Select by recurrence
    RecurrenceOptionsContent(
        color = color,
        recurrence = recurrence,
        onRecurrenceChange = onRecurrenceChange,
        onSelect = { start, end ->
            startedAtDate.value = start.toLocalDate()
            startedAtTime.value = start.toLocalTime()
            endAtDate.value = end.toLocalDate()
            endAtTime.value = end.toLocalTime()
        }
    )

    if (showDatePicker.value) {
        DateRangeDialog(
            title = "recurrence",
            color = color,
            showDatePicker = showDatePicker,
            startLocalDateState = startedAtDate,
            endLocalDateState = endAtDate
        )
    }

    RecurrenceBottomButton(
        color = color,
        transactionFieldState = transactionFieldState.value,
        onConfirm = {
            val newStart = startedAtDate.value.atTime(startedAtTime.value)
            val newEnd = endAtDate.value.atTime(endAtTime.value)

            when {
                !newEnd.isAfter(newStart) -> {
                    transactionFieldState.value =
                        TransactionFieldState.Error("End time must be after start time")
                }

                newEnd.isAfter(LocalDateTime.now().plusYears(1)) -> {
                    transactionFieldState.value =
                        TransactionFieldState.Error("End date must be within 1 year")
                }

                recurrence is RecurrencePattern.Custom && (recurrence as RecurrencePattern.Custom).days.isEmpty() -> {
                    transactionFieldState.value =
                        TransactionFieldState.Error("Select at least one day for custom recurrence")
                }

                else -> {
                    transactionFieldState.value = TransactionFieldState.Success
                    onSubmit(newStart, newEnd)
                    onDismissRequest()
                }
            }
        },
        onCancel = {
            onDismissRequest()
            showDatePicker.value = false
            showTimePicker.value = false
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
            localTimeState = if (showRecurrencePicker.value == "Starting Time") startedAtTime.value else endAtTime.value,
            onTimeChange = {
                if (showRecurrencePicker.value == "Starting Time") {
                    startedAtTime.value = it
                } else {
                    endAtTime.value = it
                }
            }
        )
    }

}

@Composable
fun RecurrenceOptionsContent(
    color: Color,
    recurrence: RecurrencePattern,
    onRecurrenceChange: (RecurrencePattern) -> Unit,
    onSelect: (start: LocalDateTime, end: LocalDateTime) -> Unit
) {
    val selectedDays = remember {
        mutableStateListOf<Int>().apply {
            if (recurrence is RecurrencePattern.Custom) {
                addAll((recurrence as RecurrencePattern.Custom).days)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Recurrence",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        LazyRow (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RecurrencePattern.entries.forEach { pattern ->
                item(key = pattern.name) {
                    RecurrenceItemButton(
                        title = pattern.name,
                        color = color,
                        isSelected = when {
                            recurrence is RecurrencePattern.Custom && pattern is RecurrencePattern.Custom -> true
                            else -> recurrence == pattern
                        },
                        onClick = {
                            val newPattern = if (pattern is RecurrencePattern.Custom) {
                                RecurrencePattern.Custom(selectedDays.toList())
                            } else {
                                pattern
                            }
                            onRecurrenceChange(newPattern)

                            val now = LocalDateTime.now()
                            val end = when (newPattern) {
                                is RecurrencePattern.DAILY -> now.plusDays(1).withHour(0).withMinute(0)
                                is RecurrencePattern.WEEKLY -> now.plusWeeks(1).withHour(0).withMinute(0)
                                is RecurrencePattern.MONTHLY -> now.plusMonths(1).withHour(0).withMinute(0)
                                is RecurrencePattern.YEARLY -> now.plusYears(1).withHour(0).withMinute(0)
                                else -> null
                            }

                            if (end != null) {
                                onSelect(now, end)
                            }
                        }
                    )
                }
            }
        }

        if (recurrence is RecurrencePattern.Custom) {
            RecurrenceWeekDay(
                color = color,
                dayState = selectedDays,
                onDaysChanged = { updatedDays ->
                    onRecurrenceChange(RecurrencePattern.Custom(updatedDays))
                }
            )
        }
    }
}

@Composable
fun RecurrenceWeekDay(
    color: Color,
    dayState: SnapshotStateList<Int>,
    onDaysChanged: (List<Int>) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Select Days",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold
        )

        Row (
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val dayNames = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            dayNames.forEachIndexed { index, day ->
                RecurrenceDayCircle(
                    title = day.take(1),
                    color = color,
                    isSelected = index in dayState,
                    onClick = {
                        if (index in dayState) {
                            dayState.remove(index)
                        } else {
                            dayState.add(index)
                        }
                        onDaysChanged(dayState.toList())
                    }
                )
            }
        }
    }
}

@Composable
fun RecurrenceDayCircle(
    title: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(36.dp)
            .clip(CircleShape)
            .background(if (isSelected) color else Color.Transparent)
            .border(
                width = 1.dp,
                color = if (isSelected) color else MaterialTheme.colorScheme.outline,
                shape = CircleShape
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecurrenceDateContent(
    title: String,
    startDateText: String,
    lastDateText: String,
    startTimeText: String,
    lastTimeText: String,
    color: Color,
    onDateClick: () -> Unit = {},
    onStartTimeClick: () -> Unit = {},
    onLastTimeClick: () -> Unit = {}
) {
    Column(
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Select a date range for this transaction",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
               primary = color
            )
        ) {

            OutlinedButton(
                onClick = onDateClick,
                colors = ButtonDefaults.outlinedButtonColors().copy(
                    contentColor = color
                ),
                border = BorderStroke(1.dp, color)
            ) {
                Text(
                    "$startDateText - $lastDateText",
                    style = MaterialTheme.typography.labelSmall,
                    color = color,
                    fontWeight = FontWeight.Bold
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onStartTimeClick,
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = color
                    ),
                    border = BorderStroke(1.dp, color)
                ) {
                    Text(
                        startTimeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
                    )
                }

                Text(
                    "to",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedButton(
                    onClick = onLastTimeClick,
                    colors = ButtonDefaults.outlinedButtonColors().copy(
                        contentColor = color
                    ),
                    border = BorderStroke(1.dp, color)
                ) {
                    Text(
                        lastTimeText,
                        style = MaterialTheme.typography.labelSmall,
                        color = color,
                        fontWeight = FontWeight.Bold
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
    transactionFieldState: TransactionFieldState,
) {
    Row(
        modifier = Modifier,
        horizontalArrangement = Arrangement.Center,
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

        Spacer(modifier = Modifier.width(5.dp))

        Button (
            onClick = onConfirm,
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            colors = ButtonDefaults.buttonColors().copy(
                containerColor = if (transactionFieldState is TransactionFieldState.Error)
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

@Composable
fun RecurrenceItemButton(
    title: String,
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val stroke = if (isSelected) {
        BorderStroke(1.dp, color)
    } else {
        BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f))
    }

    Surface (
        modifier = Modifier
            .border(stroke, MaterialTheme.shapes.medium),
        shape = MaterialTheme.shapes.medium,
        color = if (isSelected) color.copy(alpha = 0.1f) else Color.Transparent,
        onClick = onClick
    ) {
        Text(
            title,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .clip(MaterialTheme.shapes.medium)
                .padding(10.dp),
            color = if (isSelected) {
                color
            } else {
                MaterialTheme.colorScheme.onSurface
            },
            fontWeight = FontWeight.Bold
        )
    }
}
