package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import kotlinx.coroutines.flow.StateFlow
import java.time.LocalDate
import androidx.compose.runtime.collectAsState

@Composable
fun WeekView(
    pagerState: PagerState,
    weekDaysForPage: List<LocalDate>,
    selectedDate: LocalDate,
    weekNumber: Int?,
    counts: List<Int>,
    onResetClick: () -> Unit,
    onDayClick: (LocalDate) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(0.95f),
            state = pagerState
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDaysForPage.forEachIndexed { index, date ->
                    item(key = index) {
                        val countValue = counts.getOrNull(index) ?: 0

                        WeekDayView(
                            day = date,
                            count = countValue,
                            isSelected = date == selectedDate
                        ) {
                            onDayClick(date)
                        }
                    }
                }
            }
        }

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Week ${weekNumber ?: ""}",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            IconButton(onClick = onResetClick) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Reset",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun WeekDayView(
    day: LocalDate,
    count: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val materialTheme = MaterialTheme.colorScheme
    val isToday = remember(day) { day == LocalDate.now() }

    val textColor = when {
        isSelected -> materialTheme.onPrimary
        isToday -> materialTheme.secondary
        else -> materialTheme.onSurfaceVariant
    }

    val backgroundColor = if (isSelected)
        materialTheme.primary
    else Color.Transparent

    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = materialTheme.secondary,
                    contentColor = materialTheme.onSurface
                ) {
                    Text(
                        text = count.toString(),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
    ) {
        Column(
            modifier = Modifier
                .width(40.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(backgroundColor)
                .clickable { onClick() }
                .padding(vertical = 8.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = day.dayOfWeek.name.take(3),
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
            )

            HorizontalDivider(
                color = textColor,
            )

            Text(
                text = day.dayOfMonth.toString().padStart(2, '0'),
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )
        }
    }
}
