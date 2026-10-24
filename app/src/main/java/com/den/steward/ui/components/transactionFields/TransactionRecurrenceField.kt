// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.den.steward.R
import com.den.steward.backend.entitles.Recurrence
import com.den.steward.helper.formatedDateTime
import com.den.steward.helper.formattedDate
import com.den.steward.helper.formattedTime
import com.den.steward.ui.components.DateRangeDialog
import com.den.steward.ui.components.TimeDialog
import java.time.DayOfWeek
import java.time.LocalDateTime

@Composable
fun TransactionRecurrenceField(
    colorResId: Int,
    recurrence: Recurrence,
    isStartNotEqualToEndDateTime: TransactionFieldState,
    onStartTimeChange: (LocalDateTime) -> Unit,
    onEndTimeChange: (LocalDateTime) -> Unit,
    onRecurrenceChange: (Recurrence) -> Unit,
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
        color = color,
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
    recurrence: Recurrence,
    isStartNotEqualToEndDateTime: TransactionFieldState,
    onIsStartNotEqualToEndDateTimeChange: (TransactionFieldState) -> Unit,
    color: Color,
) {
    val isError = isStartNotEqualToEndDateTime is TransactionFieldState.Error

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
        headlineContent = {
            Column(
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                if (isStartNotEqualToEndDateTime is TransactionFieldState.Error) {
                    Text(
                        text = isStartNotEqualToEndDateTime.message,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(top = 2.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                if (startedAt.value != null) {
                    Text(
                        text = "Starts: ${startedAt.value!!.formatedDateTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (endAt.value != null && startedAt.value != null && endAt.value!!.isAfter(startedAt.value!!)) {
                    Text(
                        text = "Ends: ${endAt.value!!.formatedDateTime}",
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isError) MaterialTheme.colorScheme.error.copy(alpha = 0.7f) else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        },
        trailingContent = {
            val text = if (isError)
                    "Required"
            else recurrence.name
            Text(
                text = text,
                fontWeight = FontWeight.Bold,
                color = if (isError)
                    MaterialTheme.colorScheme.error
                else color,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = FONT_SIZE
                )
            )
        }
    ) {
        onDialogShow.value = true
    }
}

@Composable
private fun RecurrenceDialog(
    color: Color,
    recurrence: Recurrence,
    onRecurrenceChange: (Recurrence) -> Unit,
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
    recurrence: Recurrence,
    onRecurrenceChange: (Recurrence) -> Unit,
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
                showRecurrencePicker.value = "Starting Once"
            },
            onLastTimeClick = {
                showTimePicker.value = true
                showRecurrencePicker.value = "Ending Once"
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

                (recurrence is Recurrence.OnDays) && recurrence.days.isEmpty() -> {
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
            localTimeState = if (showRecurrencePicker.value == "Starting Once") startedAtTime.value else endAtTime.value,
            onTimeChange = {
                if (showRecurrencePicker.value == "Starting Once") {
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
    recurrence: Recurrence,
    onRecurrenceChange: (Recurrence) -> Unit,
    onSelect: (start: LocalDateTime, end: LocalDateTime) -> Unit
) {
    val selectedDays = remember(recurrence) {
        mutableStateListOf<DayOfWeek>().apply {
            if (recurrence is Recurrence.OnDays) {
                addAll(recurrence.days)
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

        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(Recurrence.presets) { preset ->
                val isSelected = recurrence == preset ||
                        (recurrence is Recurrence.OnDays && preset is Recurrence.OnDays)
                RecurrenceItemButton(
                    title = preset.name,
                    color = color,
                    isSelected = isSelected,
                    onClick = {
                        val now = LocalDateTime.now()
                        val end: LocalDateTime? = when (preset) {
                            is Recurrence.Every -> when (preset) {
                                Recurrence.Daily -> now.plusDays(1).withHour(23).withMinute(59)
                                    .withSecond(59)

                                Recurrence.Weekly -> now.plusWeeks(1).withHour(23).withMinute(59)
                                    .withSecond(59)

                                Recurrence.Monthly -> now.plusMonths(1).withHour(23).withMinute(59)
                                    .withSecond(59)

                                Recurrence.Yearly -> now.plusYears(1).withHour(23).withMinute(59)
                                    .withSecond(59)

                                else -> now.plus(1, preset.unit).withHour(23).withMinute(59)
                                    .withSecond(59)
                            }

                            is Recurrence.OnDays -> {
                                if (selectedDays.isNotEmpty()) {
                                    val todayDay = now.dayOfWeek
                                    val minOffset =
                                        selectedDays.minOfOrNull { (it.value - todayDay.value + 7) % 7 }
                                            ?: 0
                                    val daysToAdd = if (minOffset == 0) 7 else minOffset
                                    now.plusDays(daysToAdd.toLong()).withHour(23).withMinute(59)
                                        .withSecond(59)
                                } else null
                            }

                            is Recurrence.Once -> now.plusMinutes(2)
                        }

                        val newRecurrence = if (isSelected) {
                            Recurrence.NONE
                        } else {
                            when (preset) {
                                is Recurrence.OnDays -> Recurrence.OnDays(selectedDays.toSet())
                                else -> preset
                            }
                        }
                        onRecurrenceChange(newRecurrence)

                        if (end != null) {
                            onSelect(now, end)
                        }
                    }
                )
            }
        }

        if (recurrence is Recurrence.OnDays) {
            RecurrenceWeekDay(
                color = color,
                dayState = selectedDays,
                onDaysChanged = { updatedDays ->
                    onRecurrenceChange(Recurrence.OnDays(updatedDays.toSet()))

                    if (updatedDays.isNotEmpty()) {
                        val now = LocalDateTime.now()
                        val todayDay = now.dayOfWeek
                        val minOffset =
                            updatedDays.minOfOrNull { (it.value - todayDay.value + 7) % 7 } ?: 0
                        val daysToAdd = if (minOffset == 0) 7 else minOffset
                        val end = now.plusDays(daysToAdd.toLong()).withHour(23).withMinute(59)
                            .withSecond(59)
                        onSelect(now, end)
                    }
                }
            )
        }
    }
}

@Composable
fun RecurrenceWeekDay(
    color: Color,
    dayState: SnapshotStateList<DayOfWeek>,
    onDaysChanged: (List<DayOfWeek>) -> Unit
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
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val daysInWeek = listOf(
                DayOfWeek.SUNDAY,
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                DayOfWeek.WEDNESDAY,
                DayOfWeek.THURSDAY,
                DayOfWeek.FRIDAY,
                DayOfWeek.SATURDAY
            )
            val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                daysInWeek.forEachIndexed { index, dayOfWeek ->
                    val isSelected = dayOfWeek in dayState
                    item(key = index) {
                        RecurrenceDayCircle(
                            title = dayLabels[index],
                            color = color,
                            isSelected = isSelected,
                            onClick = {
                                if (isSelected) {
                                    dayState.remove(dayOfWeek)
                                } else {
                                    dayState.add(dayOfWeek)
                                }
                                onDaysChanged(dayState.toList())
                            }
                        )
                    }
                }
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

    val boxColor = if (isSelected) color else Color.LightGray


    Surface(
        onClick = onClick,
        color = Color.Transparent,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) color else MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .width(25.dp)
                    .height(10.dp)
                    .background(boxColor),
            )
        }
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
    val border = FilterChipDefaults.filterChipBorder(
        enabled = true,
        selected = isSelected,
        selectedBorderColor = color
    )

    FilterChip(
        shape = MaterialTheme.shapes.medium,
        onClick = onClick,
        selected = isSelected,
        colors = FilterChipDefaults.filterChipColors().copy(
            selectedContainerColor = color.copy(0.2f),
            selectedLabelColor = color,
        ),
        label = {
            Text(
                title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
        },
        border = border,
        leadingIcon = {
            if (isSelected) {
                androidx.compose.material3.Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = color
                )
            }
        }
    )
}
