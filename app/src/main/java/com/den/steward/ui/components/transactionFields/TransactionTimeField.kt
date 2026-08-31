// Grace and truth came through JESUS
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
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
    localTime: LocalTime,
    onLocalTimeChange: (LocalTime) -> Unit,
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
                localTimeState = localTime,
                onTimeChange = onLocalTimeChange
            )
        }
    }

    TransactionTimeFieldItem(
        modifier = modifier,
        showTimePicker = showTimePicker,
        displayState = localTime,
        color = color,
        onTimeChange = onLocalTimeChange
    )
}

@Composable
private fun TransactionTimeFieldItem(
    color: Color,
    modifier: Modifier = Modifier,
    showTimePicker: MutableState<Boolean>,
    displayState: LocalTime,
    onTimeChange: (LocalTime) -> Unit
) {
    val isNowActive = remember(displayState) {
        val currentTime = LocalTime.now()
        displayState.hour == currentTime.hour && displayState.minute == currentTime.minute
    }

    val onBackground = MaterialTheme.colorScheme.onBackground
    val colorState = if (isNowActive) color else onBackground
    val border = if (isNowActive) BorderStroke(1.dp, color) else null

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
            val textValue = displayState.formattedTime
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                Text(textValue, fontSize = FONT_SIZE)
                Spacer(modifier = Modifier.size(4.dp))
                TextButton(
                    onClick = {
                        onTimeChange(LocalTime.now())
                    },
                    contentPadding = PaddingValues(0.dp),
                    border = border,
                    shape = MaterialTheme.shapes.small,
                ) {
                    Text(
                        "Now",
                        style = MaterialTheme.typography.labelMedium,
                        color = colorState,
                        modifier = Modifier.padding(horizontal = 8.dp)
                    )
                }
            }
        }
    ) {
        showTimePicker.value = true
    }
}
