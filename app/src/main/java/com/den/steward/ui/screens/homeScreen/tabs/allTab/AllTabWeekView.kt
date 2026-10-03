package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBackIosNew
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.den.steward.backend.states.PeriodType
import com.den.steward.helper.formattedDate
import com.den.steward.ui.components.FloatingBottomSheetCalendar
import com.kizitonwose.calendar.core.CalendarDay
import java.time.LocalDate
import kotlinx.collections.immutable.ImmutableList

@Composable
fun WeekView(
    pagerState: PagerState,
    periodType: PeriodType,
    isSearchExpanded: Boolean,
    searchState: TextFieldState,
    weekDaysForPage: List<LocalDate>,
    selectedDate: LocalDate,
    weekNumber: Int?,
    counts: ImmutableList<Pair<Int?, Int>>,
    saveSearchQuery: (text: String) -> Unit,
    onShowRecentSearch: (show: Boolean) -> Unit,
    onResetClick: () -> Unit,
    onSearchShow: (show: Boolean) -> Unit,
    moreInfo: @Composable (ColumnScope.(day: CalendarDay) -> Unit) = {},
    onDayClick: (LocalDate) -> Unit,
) {
    val onCalenderShow = remember { mutableStateOf(false) }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        HorizontalPager(
            modifier = Modifier.fillMaxWidth(0.95f),
            state = pagerState,
            beyondViewportPageCount = 1
        ) {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                weekDaysForPage.forEachIndexed { index, date ->
                    item(key = index) {
                        val (flowIndicator, countValue) = counts.getOrNull(index) ?: (null to 0)

                        WeekDayView(
                            day = date,
                            count = countValue,
                            flowIndicator = flowIndicator,
                            isSelected = date == selectedDate
                        ) {
                            onDayClick(date)
                        }
                    }
                }
            }
        }

        ControllerPanel(
            periodType = periodType,
            weekNumber = weekNumber,
            searchState = searchState,
            isSearchExpanded = isSearchExpanded,
            selectedDate = selectedDate,
            saveSearchQuery = saveSearchQuery,
            onResetClick = onResetClick,
            onCalenderShow = onCalenderShow,
            onSearchShow = onSearchShow,
            onShowRecentSearch = onShowRecentSearch
        )
    }

    FloatingBottomSheetCalendar(
        isExpanded = onCalenderShow.value,
        selectedDate = selectedDate,
        onDateSelected = onDayClick,
        onDismiss = {
            onCalenderShow.value = false
        },
        moreInfo = moreInfo
    )
}

@Composable
fun ControllerPanel(
    periodType: PeriodType,
    weekNumber: Int?,
    onCalenderShow: MutableState<Boolean>,
    searchState: TextFieldState,
    isSearchExpanded: Boolean,
    selectedDate: LocalDate,
    onResetClick: () -> Unit,
    saveSearchQuery: (text: String) -> Unit,
    onSearchShow: (show: Boolean) -> Unit,
    onShowRecentSearch: (show: Boolean) -> Unit
) {
    val focusRequester = remember { FocusRequester() }
    val keyboardController = LocalSoftwareKeyboardController.current

    LaunchedEffect(isSearchExpanded) {
        if (isSearchExpanded) {
            focusRequester.requestFocus()
        } else {
            keyboardController?.hide()
        }
    }

    val searchIconSize = 20.dp

    AnimatedContent(
        targetState = isSearchExpanded,
        transitionSpec = {
            fadeIn() togetherWith fadeOut()
        },
        label = "SearchExpandTransition"
    ) { expanded ->
        if (!expanded) {
            when (periodType) {
                PeriodType.WEEK -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { onSearchShow(true) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

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

                else -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val date = selectedDate.formattedDate
                        IconButton(
                            onClick = { onSearchShow(true) }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        OutlinedButton(
                            onClick = { onCalenderShow.value = true }
                        ) {
                            Text(
                                text = date,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

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
        } else {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    state = searchState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                    placeholder = {
                        Text(
                            text = "Search",
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    shape = CircleShape,
                    trailingIcon = {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End
                        ) {

                            IconButton(
                                onClick = {
                                    saveSearchQuery(searchState.text.toString())
                                    keyboardController?.hide()
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.size(searchIconSize)
                                )
                            }

                            if (searchState.text.isNotEmpty()) {

                                IconButton(
                                    onClick = {
                                        searchState.clearText()
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear",
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                        modifier = Modifier.size(searchIconSize)
                                    )
                                }
                            }
                        }
                    },
                    leadingIcon = {
                        IconButton(onClick = {
                            keyboardController?.hide()
                            searchState.clearText()
                            onShowRecentSearch(false)
                            onSearchShow(false)
                        }) {
                            Icon(
                                imageVector = Icons.Default.ArrowBackIosNew,
                                contentDescription = "Close Search",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(searchIconSize)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Search
                    ),
                    onKeyboardAction = {
                        saveSearchQuery(searchState.text.toString())
                        keyboardController?.hide()
                    }
                )
            }
        }
    }
}


@Composable
private fun WeekDayView(
    day: LocalDate,
    count: Int,
    flowIndicator: Int?,
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

    val badgeColor = when (flowIndicator) {
        -1 -> materialTheme.error
        1 -> materialTheme.secondary
        else -> materialTheme.secondary
    }

    val dayOfWeek = remember(day.dayOfWeek) {
        day.dayOfWeek.name.take(3)
    }
    val dayOfMonth = remember(day.dayOfMonth) {
        day.dayOfMonth.toString().padStart(2, '0')
    }

    BadgedBox(
        badge = {
            if (count > 0) {
                Badge(
                    containerColor = badgeColor,
                    contentColor = MaterialTheme.colorScheme.surface
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
                text = dayOfWeek,
                style = MaterialTheme.typography.labelMedium,
                color = textColor,
            )

            HorizontalDivider(
                color = textColor,
            )

            Text(
                text = dayOfMonth,
                style = MaterialTheme.typography.titleMedium,
                color = textColor,
            )
        }
    }
}
