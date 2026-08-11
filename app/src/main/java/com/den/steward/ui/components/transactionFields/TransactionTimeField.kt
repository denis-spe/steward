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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.helper.formattedTime
import com.den.steward.ui.components.TimeDialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionTimeField(
    title: String,
    colorResId: Int,
    modifier: Modifier = Modifier,
    localTimeState: MutableState<LocalTime>,
) {
    val showTimePicker = remember { mutableStateOf(false) }
    val color = colorResource(id = colorResId)

    if (showTimePicker.value) {
        MaterialTheme(
            colorScheme = MaterialTheme.colorScheme.copy(
                primary = color,
            )
        ) {
            TimeDialog(
                title = title,
                color = color,
                showTimePicker = showTimePicker,
                localTimeState = localTimeState
            )
        }
    }

    TransactionTimeFieldItem(
        modifier = modifier,
        showTimePicker = showTimePicker,
        displayState = localTimeState,
        color = color
    )
}

@Composable
private fun TransactionTimeFieldItem(
    color: Color,
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

    TransactionFieldCard(
        title = "Time",
        modifier = modifier,
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.clock),
                contentDescription = "clock",
                modifier = Modifier.size(ICON_SIZE)
            )
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
                        BorderStroke(1.dp, color)
                    else null,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "Now",
                        style = MaterialTheme.typography.labelMedium,
                        color = if (isNowClick.value) {
                            color
                        } else {
                            MaterialTheme.colorScheme.onBackground
                        },
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    ) {
        showTimePicker.value = true
    }
}
