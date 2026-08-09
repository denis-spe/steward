// Glory be to LORD our GOD
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.helper.formattedDate
import com.den.steward.helper.toEpochMillis
import com.den.steward.helper.yesterday
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZoneOffset

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateField(
    title: String,
    color: Color,
    modifier: Modifier = Modifier,
    localDateState: MutableState<LocalDate>,
) {
    val showDatePicker = remember { mutableStateOf(false) }

    if (showDatePicker.value) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = localDateState.value
                .atStartOfDay(ZoneOffset.UTC)
                .toInstant()
                .toEpochMilli()
        )
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
                        val selectedDateMillis = datePickerState.selectedDateMillis
                        if (selectedDateMillis != null) {
                            localDateState.value = Instant.ofEpochMilli(selectedDateMillis)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()
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
                            text = "Date",
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

    TransactionDateFieldItem(
        modifier = modifier,
        showDatePicker = showDatePicker,
        displayState = localDateState,
    )
}

@Composable
private fun TransactionDateFieldItem(
    modifier: Modifier = Modifier,
    showDatePicker: MutableState<Boolean>,
    displayState: MutableState<LocalDate>,
) {
    val today = LocalDate.now()
    val yesterday = LocalDateTime.now()
        .yesterday().toLocalDate()

    val wasTodayYesterdayClick = remember { mutableStateOf("Initial") }

    LaunchedEffect(displayState.value) {
        wasTodayYesterdayClick.value = when (displayState.value) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> "Initial"
        }
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
                    showDatePicker.value = true
                },
            leadingContent = {
                Image(
                    painter = painterResource(R.drawable.calendar),
                    contentDescription = "calendar",
                    modifier = Modifier.size(ICON_SIZE)
                )
            },

            headlineContent = {
                Text("Date", fontSize = FONT_SIZE, fontWeight = FONT_WEIGHT)
            },

            trailingContent = {
                val textValue = displayState.value.formattedDate
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(textValue, fontSize = FONT_SIZE)
                    Spacer(modifier = Modifier.size(4.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextButton(
                            onClick = {
                                displayState.value = LocalDate.now()
                            },
                            contentPadding = PaddingValues(0.dp),
                            border = if (wasTodayYesterdayClick.value == "Today")
                                BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                            else null,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                "Today",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                        }

                        Spacer(modifier = Modifier.size(8.dp))

                        TextButton(
                            onClick = {
                                displayState.value = yesterday
                            },
                            contentPadding = PaddingValues(0.dp),
                            border = if (wasTodayYesterdayClick.value == "Yesterday")
                                BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                            else null,
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Text(
                                "Yesterday",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.padding(4.dp)
                            )
                        }
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