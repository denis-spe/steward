// Glory be to LORD our GOD
package com.den.steward.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@Composable
fun DateDialog(
    title: String,
    color: Color,
    headerTitle: String = "Date",
    showDatePicker: MutableState<Boolean>,
    localDateState: LocalDate,
    onDateChange: (LocalDate) -> Unit
) {
    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = localDateState
            .atStartOfDay(ZoneOffset.UTC)
            .toInstant()
            .toEpochMilli()
    )
    val scrollState = rememberScrollState()

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = color)
    ) {
        DatePickerDialog(
            modifier = Modifier.verticalScroll(scrollState),
            onDismissRequest = {
                showDatePicker.value = false
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDatePicker.value = false
                    }
                ) {
                    Text(
                        "Cancel",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
            confirmButton = {

                Button(
                    onClick = {
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            onDateChange(Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate())
                        }
                        showDatePicker.value = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    )
                ) {
                    Text(
                        "OK",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        ) {
            DatePicker(
                state = datePickerState,
                headline = {
                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = color,
                        )
                        Text(
                            text = "Set date for $title",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                        )
                    }
                },
                colors = DatePickerDefaults.colors(
                    titleContentColor = color,
                    headlineContentColor = color,
                    todayContentColor = color,
                    todayDateBorderColor = color,
                    selectedDayContainerColor = color,
                )
            )
        }
    }
}