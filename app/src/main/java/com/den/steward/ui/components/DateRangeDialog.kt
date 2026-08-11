// Love the LORD your GOD with all your soul and with all your mind
// and with all your strenght and love your neighbor as yourself
package com.den.steward.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeDialog(
    title: String,
    color: Color,
    showDatePicker: MutableState<Boolean>,
    startLocalDateState: MutableState<LocalDate>,
    endLocalDateState: MutableState<LocalDate>,
) {
    // Initializing with current UI values, ensuring UTC for DateRangePicker
    val datePickerState = rememberDateRangePickerState(
        initialSelectedStartDateMillis = startLocalDateState.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli(),
        initialSelectedEndDateMillis = endLocalDateState.value.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
    )

    MaterialTheme(
        colorScheme = MaterialTheme.colorScheme.copy(primary = color)
    ) {
        DatePickerDialog(
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
                        val startMillis = datePickerState.selectedStartDateMillis
                        val endMillis = datePickerState.selectedEndDateMillis

                        if (startMillis != null && endMillis != null) {
                            // Converting UTC millis back to LocalDate
                            startLocalDateState.value = Instant.ofEpochMilli(startMillis)
                                .atZone(ZoneOffset.UTC).toLocalDate()
                            endLocalDateState.value = Instant.ofEpochMilli(endMillis)
                                .atZone(ZoneOffset.UTC).toLocalDate()
                            showDatePicker.value = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = color
                    ),
                    enabled = datePickerState.selectedStartDateMillis != null && 
                             datePickerState.selectedEndDateMillis != null
                ) {
                    Text(
                        "OK",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            },
        ) {
            DateRangePicker(
                state = datePickerState,
                title = {
                    Text(
                        text = "Select Range",
                        modifier = Modifier.padding(start = 16.dp, top = 16.dp)
                    )
                },
                headline = {
                    Text(
                        text = "Set a range for $title",
                        style = MaterialTheme.typography.titleSmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                },
                showModeToggle = false,
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
