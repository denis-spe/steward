// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.outlined.ModeEdit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerDialog
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.helper.formattedTime
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTimeField(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    localTimeState: MutableState<LocalTime>,
) {
    val showTimePicker = remember { mutableStateOf(false) }
    val timePickerMode = remember { mutableStateOf("Clock") }

    if (showTimePicker.value) {
        val timePickerState = rememberTimePickerState(
            initialHour = localTimeState.value.hour,
            initialMinute = localTimeState.value.minute,
            is24Hour = true
        )
        TimePickerDialog(
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
                                text = "Time",
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
                        localTimeState.value = LocalTime.of(
                            timePickerState.hour,
                            timePickerState.minute
                        )
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
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            },
        ) {
            val pickerColors = TimePickerDefaults.colors(
                clockDialColor = MaterialTheme.colorScheme.surfaceVariant,
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
                TimeInput(
                    state = timePickerState,
                    colors = pickerColors,
                )
            }
    }
    }

    TransactionTimeFieldItem(
        modifier = modifier,
        showTimePicker = showTimePicker,
        displayState = localTimeState,
    )
}

@Composable
private fun TransactionTimeFieldItem(
    modifier: Modifier = Modifier,
    showTimePicker: MutableState<Boolean>,
    displayState: MutableState<LocalTime>,
) {
    val isNowClick = remember { mutableStateOf(false) }

    LaunchedEffect(displayState.value) {
        val now = LocalTime.now()
        isNowClick.value = displayState.value.hour == now.hour &&
                displayState.value.minute == now.minute
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        ListItem(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .height(LIST_ITEM_HEIGHT)
                .clickable {
                    showTimePicker.value = true
                },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.clock),
                    contentDescription = "clock",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },

            headlineContent = {
                Text("Time", fontSize = FONT_SIZE, fontWeight = FONT_WEIGHT)
            },

            trailingContent = {
                val textValue = displayState.value.formattedTime
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(textValue, fontSize = FONT_SIZE)
                    Spacer(modifier = Modifier.size(4.dp))
                    TextButton(
                        onClick = {
                            displayState.value = LocalTime.now()
                        },
                        contentPadding = PaddingValues(0.dp),
                        border = if (isNowClick.value)
                            BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                        else null,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            "Now",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }
        )

        HorizontalDivider(
            modifier = Modifier.padding(bottom = 2.dp),
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}
