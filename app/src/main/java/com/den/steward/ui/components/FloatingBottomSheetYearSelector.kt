// Glory be to LORD GOD
package com.den.steward.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.collections.immutable.toImmutableList
import java.time.LocalDate
import java.time.YearMonth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingBottomSheetYearSelector(
    isExpanded: Boolean = true,
    selectedDate: LocalDate? = null,
    onYearSelected: (LocalDate) -> Unit = {},
    onDismiss: () -> Unit = {},
    counts: Pair<Map<Int, Int>, Map<Int, Int>>? = null
) {
    if (!isExpanded) return

    val currentYearMonth = remember { YearMonth.now() }
    val initialDate = selectedDate ?: currentYearMonth.atDay(1)
    var selectedYear by remember(selectedDate) { mutableStateOf(selectedDate?.year) }

    val years = remember {
        (currentYearMonth.minusYears(15).year + 1..
                currentYearMonth.plusYears(10).year).toImmutableList()
    }



    FloatingModelBottomSheet(
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
        ) {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Year",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Column {
                    Text(
                        "Current Year",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        currentYearMonth.year.toString(),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            // Months Grid
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .padding(16.dp),
                contentPadding = PaddingValues(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(years.size, key = { years[it] }) { index ->
                    val year = years[index]
                    val isSelected = selectedYear == year
                    val flow = counts?.first?.get(year) ?: 0
                    val count = counts?.second?.get(year) ?: 0

                    YearButton(
                        year = year,
                        count = count,
                        color = when {
                            flow < 0 -> MaterialTheme.colorScheme.error
                            flow > 0 -> Color(0xFF3BA429)
                            else -> MaterialTheme.colorScheme.primaryContainer
                        },
                        isCurrentYear = currentYearMonth.year == year,
                        isSelected = isSelected,
                        onClick = {
                            selectedYear = year
                            val selectedLocalDate = LocalDate.of(year, initialDate.month, 1)
                            onYearSelected(selectedLocalDate)
                            onDismiss()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun YearButton(
    year: Int,
    count: Int = 0,
    color: Color = MaterialTheme.colorScheme.primaryContainer,
    isSelected: Boolean = false,
    selectedColor: Color = MaterialTheme.colorScheme.primary,
    currentColor: Color = MaterialTheme.colorScheme.primary,
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
            border = if (isCurrentYear && !isSelected) BorderStroke(1.dp, currentColor) else null,
            color = backgroundColor,
            contentColor = contentColor
        ) {
            Row(
                modifier = Modifier
                    .width(100.dp)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = year.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = if (isSelected || isCurrentYear) FontWeight.Bold else FontWeight.Normal,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
