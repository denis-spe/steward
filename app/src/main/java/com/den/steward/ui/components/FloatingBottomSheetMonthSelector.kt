// Let the LORD be magnified
package com.den.steward.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.den.steward.helper.title
import java.time.LocalDate
import java.time.Month
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingBottomSheetMonthSelector(
    isExpanded: Boolean = true,
    selectedDate: LocalDate? = null,
    onMonthSelected: (LocalDate) -> Unit = {},
    onDismiss: () -> Unit = {},
    counts: Pair<Map<Month, Int>, Map<Month, Int>>? = null
) {
    if (!isExpanded) return

    val currentYearMonth = remember { YearMonth.now() }
    val initialDate = selectedDate ?: currentYearMonth.atDay(1)

    var displayedYear by remember(selectedDate) { mutableIntStateOf(initialDate.year) }
    var selectedMonth by remember(selectedDate) { mutableStateOf<Month?>(selectedDate?.month) }

    val monthNames = remember { Month.entries }

    FloatingModelBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Year Navigation Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                IconButton(onClick = { displayedYear-- }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Previous Year"
                    )
                }

                Text(
                    text = displayedYear.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                IconButton(onClick = { displayedYear++ }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Next Year"
                    )
                }
            }

            // Months Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(monthNames.size, key = { monthNames[it] }) { index ->
                    val month = monthNames[index]
                    val isSelected = selectedMonth == month && displayedYear == (selectedDate?.year
                        ?: initialDate.year)
                    val isCurrentMonth =
                        currentYearMonth.year == displayedYear && currentYearMonth.month == month
                    val flow = counts?.first?.get(month) ?: 0
                    val count = counts?.second?.get(month) ?: 0

                    MonthButton(
                        month = month,
                        count = count,
                        color = when {
                            flow < 0 -> MaterialTheme.colorScheme.error
                            flow > 0 -> Color(0xFF3BA429)
                            else -> Color.DarkGray
                        },
                        isCurrentYear = currentYearMonth.year == displayedYear,
                        isSelected = isSelected,
                        isCurrentMonth = isCurrentMonth,
                        onClick = {
                            selectedMonth = month
                            val selectedLocalDate = LocalDate.of(displayedYear, month, 1)
                            onMonthSelected(selectedLocalDate)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MonthButton(
    month: Month,
    count: Int = 0,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    currentColor: Color = MaterialTheme.colorScheme.primary,
    isCurrentMonth: Boolean = false,
    onClick: () -> Unit,
    isCurrentYear: Boolean
) {
    val backgroundColor = when {
        isSelected -> selectedColor
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }

    val contentColor = when {
        isSelected -> MaterialTheme.colorScheme.onPrimary
        else -> MaterialTheme.colorScheme.onSurfaceVariant
    }

    BadgedBox(
        badge = {
            if (count > 0 && isCurrentYear) {
                Badge(
                    containerColor = color
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    ) {
        Surface(
            onClick = onClick,
            shape = CircleShape,
            border = if (isCurrentMonth && !isSelected) BorderStroke(1.dp, currentColor) else null,
            color = backgroundColor,
            contentColor = contentColor
        ) {
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = month.name.title,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected || isCurrentMonth) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
