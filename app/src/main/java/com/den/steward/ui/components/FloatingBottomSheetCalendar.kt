package com.den.steward.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.kizitonwose.calendar.compose.HorizontalCalendar
import com.kizitonwose.calendar.compose.rememberCalendarState
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FloatingBottomSheetCalendar(
    isExpanded: Boolean = true,
    selectedDate: LocalDate? = null,
    onDateSelected: (LocalDate) -> Unit = {},
    onDismiss: () -> Unit = {},
    moreInfo: @Composable ColumnScope.(day: CalendarDay) -> Unit = {}
) {
    if (!isExpanded) return

    val currentMonth = remember { YearMonth.now() }
    val startMonth = remember { currentMonth.minusMonths(100) }
    val endMonth = remember { currentMonth.plusMonths(100) }
    val firstDayOfWeek = remember { firstDayOfWeekFromLocale() }

    val state = rememberCalendarState(
        startMonth = startMonth,
        endMonth = endMonth,
        firstVisibleMonth = currentMonth,
        firstDayOfWeek = firstDayOfWeek,
    )

    var currentSelectedDate by remember { mutableStateOf(selectedDate) }

    FloatingModelBottomSheet(
        onDismiss = onDismiss
    ) {
        HorizontalCalendar(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
                .padding(10.dp),
            state = state,
            dayContent = { day ->
                Day(
                    day = day,
                    isSelected = day.date == currentSelectedDate,
                    onClick = { clickedDay ->
                        currentSelectedDate = clickedDay.date
                        onDateSelected(clickedDay.date)
                    }
                ) {
                    moreInfo(day)
                }
            },
            monthHeader = { month ->
                val daysOfWeek = remember(month) {
                    month.weekDays.first().map { it.date.dayOfWeek }
                }
                MonthHeader(
                    calendarMonth = month,
                    daysOfWeek = daysOfWeek
                )
            }
        )
    }
}

@Composable
fun MonthHeader(
    calendarMonth: CalendarMonth,
    daysOfWeek: List<DayOfWeek>
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${
                calendarMonth.yearMonth.month.name.lowercase().replaceFirstChar { it.titlecase() }
            } ${calendarMonth.yearMonth.year}",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            daysOfWeek.forEach { dayOfWeek ->
                DayHeader(
                    modifier = Modifier.weight(1f),
                    dayOfWeek = dayOfWeek
                )
            }
        }
    }
}

@Composable
fun DayHeader(
    modifier: Modifier = Modifier,
    dayOfWeek: DayOfWeek
) {
    val locale = LocalConfiguration.current.locales[0]
    Text(
        text = dayOfWeek.getDisplayName(TextStyle.SHORT, locale),
        modifier = modifier,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
fun Day(
    day: CalendarDay,
    isSelected: Boolean = false,
    defaultColor: Color = MaterialTheme.colorScheme.secondary,
    defaultShape: Shape = MaterialTheme.shapes.medium,
    onClick: (CalendarDay) -> Unit = {},
    moreInfo: @Composable ColumnScope.() -> Unit = {}
) {
    val isToday = remember(day) { day.date == LocalDate.now() }
    val isCurrentMonth = day.position == DayPosition.MonthDate

    val textColor = when {
        !isCurrentMonth -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isToday -> defaultColor
        else -> MaterialTheme.colorScheme.onSurface
    }

    val backgroundColor = when {
        isSelected -> defaultColor
        else -> Color.Transparent
    }

    val border = if (isToday && !isSelected) {
        BorderStroke(1.dp, defaultColor)
    } else null

    Box(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(defaultShape)
            .background(backgroundColor)
            .then(if (border != null) Modifier.border(border, defaultShape) else Modifier)
            .clickable(enabled = isCurrentMonth) { onClick(day) },
        contentAlignment = Alignment.Center
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = day.date.dayOfMonth.toString(),
                color = textColor,
                style = MaterialTheme.typography.bodyMedium
            )

            moreInfo()
        }
    }
}
