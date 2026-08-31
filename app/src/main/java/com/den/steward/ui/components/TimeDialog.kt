// Glory be to LORD our GOD
package com.den.steward.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimeDialog(
    title: String,
    color: Color,
    headerTitle: String = "Time",
    showTimePicker: MutableState<Boolean>,
    localTimeState: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val timePickerMode = remember { mutableStateOf("Clock") }
    val scrollState = rememberScrollState()
    val timePickerState = rememberTimePickerState(
        initialHour = localTimeState.hour,
        initialMinute = localTimeState.minute,
        is24Hour = true
    )

    TimePickerDialog(
        modifier = Modifier.verticalScroll(scrollState),
        title = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = headerTitle,
                            style = MaterialTheme.typography.titleLarge,
                            color = color,
                        )
                        Text(
                            text = "Set time for $title",
                            style = MaterialTheme.typography.titleSmall,
                            color = Color.Gray,
                        )
                    }

                    IconButton(
                        onClick = {
                            timePickerMode.value =
                                if (timePickerMode.value == "TimePicker") "Clock" else "TimePicker"
                        }
                    ) {
                        Icon(
                            imageVector = if (timePickerMode.value == "TimePicker")
                                Icons.Outlined.ModeEdit else Icons.Default.AccessTime,
                            contentDescription = "mode",
                            tint = color
                        )
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp),
                    color = Color.Gray
                )
            }
        },
        onDismissRequest = {
            showTimePicker.value = false
        },
        dismissButton = {
            TextButton(
                onClick = {
                    showTimePicker.value = false
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
                     onTimeChange(LocalTime.of(
                        timePickerState.hour,
                        timePickerState.minute
                     ))
                    showTimePicker.value = false
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = color
                )
            ) {
                Text(
                    "OK",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        },
    ) {
        val pickerColors = TimePickerDefaults.colors(
            clockDialColor = color.copy(alpha = 0.1f),
            clockDialSelectedContentColor = MaterialTheme.colorScheme.onPrimary,
            clockDialUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            selectorColor = color,
            containerColor = MaterialTheme.colorScheme.surface,
            periodSelectorBorderColor = color,
            periodSelectorSelectedContainerColor = color.copy(alpha = 0.2f),
            periodSelectorUnselectedContainerColor = Color.Transparent,
            periodSelectorSelectedContentColor = color,
            periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            timeSelectorSelectedContainerColor = color.copy(alpha = 0.2f),
            timeSelectorUnselectedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            timeSelectorSelectedContentColor = color,
            timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        if (timePickerMode.value == "Clock") {
            TimePicker(
                state = timePickerState,
                colors = pickerColors,
            )
        } else {
            MaterialTheme(
                colorScheme = MaterialTheme.colorScheme.copy(
                    primary = color,
                    onPrimary = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                TimeInput(
                    state = timePickerState,
                    colors = pickerColors,
                )
            }
        }
    }
}