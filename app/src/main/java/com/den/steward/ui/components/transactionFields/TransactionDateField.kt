// Glory be to LORD our GOD
package com.den.steward.ui.components.transactionFields

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
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
import com.den.steward.helper.formattedDate
import com.den.steward.helper.yesterday
import com.den.steward.ui.components.DateDialog
import java.time.LocalDate
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionDateField(
    title: String,
    colorResId: Int,
    modifier: Modifier = Modifier,
    localDateState: LocalDate,
    onLocalDateChange: (LocalDate) -> Unit,
) {
    val showDatePicker = remember { mutableStateOf(false) }
    val color = colorResource(id = colorResId)

    if (showDatePicker.value) {
        DateDialog(
            title = title,
            color = color,
            showDatePicker = showDatePicker,
            localDateState = localDateState,
            onDateChange = onLocalDateChange
        )
    }

    TransactionDateFieldItem(
        color = color,
        modifier = modifier,
        showDatePicker = showDatePicker,
        displayState = localDateState,
        updateDisplayState = onLocalDateChange
    )
}

@Composable
private fun TransactionDateFieldItem(
    color: Color,
    modifier: Modifier = Modifier,
    showDatePicker: MutableState<Boolean>,
    displayState: LocalDate,
    updateDisplayState: (LocalDate) -> Unit,
) {
    val yesterday = remember { LocalDateTime.now().yesterday().toLocalDate() }

    val clickedType = remember(displayState) {
        val today = LocalDate.now()
        when (displayState) {
            today -> "Today"
            yesterday -> "Yesterday"
            else -> "Initial"
        }
    }

    val onBackground = MaterialTheme.colorScheme.onBackground
    val colorState = if (clickedType == "Today") color else onBackground

    TransactionFieldCard(
        title = "Date",
        modifier = modifier,
        leadingContent = {
            Image(
                painter = painterResource(R.drawable.calendar),
                contentDescription = "calendar",
                modifier = Modifier.size(ICON_SIZE)
            )
        },
        trailingContent = {
            val textValue = displayState.formattedDate
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
                            updateDisplayState(LocalDate.now())
                        },
                        contentPadding = PaddingValues(0.dp),
                        border = if (clickedType == "Today")
                            BorderStroke(1.dp, color)
                        else null,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            "Today",
                            style = MaterialTheme.typography.labelMedium,
                            color = colorState
                        )
                    }

                    Spacer(modifier = Modifier.size(8.dp))

                    TextButton(
                        onClick = {
                            updateDisplayState(yesterday)
                        },
                        contentPadding = PaddingValues(0.dp),
                        border = if (clickedType == "Yesterday")
                            BorderStroke(1.dp, color)
                        else null,
                        shape = MaterialTheme.shapes.small,
                    ) {
                        Text(
                            "Yesterday",
                            style = MaterialTheme.typography.labelMedium,
                            color = if (clickedType == "Yesterday")
                                color else onBackground,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }
            }
        }
    ) {
        showDatePicker.value = true
    }
}