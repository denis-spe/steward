// Holy, holy, holy is the LORD GOD of host and his right hand is
// full of holness
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.setTextAndPlaceCursorAtEnd
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.states.Filter
import com.den.steward.backend.states.Filter.Companion.icon
import com.den.steward.backend.states.OrderBy
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.states.SortAndFilterState
import com.den.steward.backend.states.SortBy
import com.den.steward.backend.states.allTabState.AllUiState
import com.den.steward.ui.theme.ExtendedTheme
import kotlinx.collections.immutable.ImmutableList

private data class AllTabControllerPanelUiState(
    val filterIcon: Int,
    val orderByIcon: Int,
    val sortByIcon: Int,
    val periodTypeIcon: Int,
    val transactionListSortName: String,
    val transactionListSortIcon: Int,
    val filterSelected: Boolean,
    val orderBySelected: Boolean,
    val sortBySelected: Boolean,
    val periodTypeSelected: Boolean,
    val isTransactionListSortSelected: Boolean,
    val isRecentSearchExpanded: Boolean,
    val isSearchExpanded: Boolean
)

@Composable
fun AllTabControllerPanel(
    sortAndFilterState: SortAndFilterState,
    allUiState: AllUiState,
    recentSearches: ImmutableList<String>,
    onShowRecentSearch: (show: Boolean) -> Unit,
    onClearRecentSearch: () -> Unit,
    onFilterClick: () -> Unit,
    onSortByClick: () -> Unit,
    onOrderByClick: () -> Unit,
    onPeriodTypeClick: () -> Unit,
    onTransactionListSort: () -> Unit,
) {
    val state = remember(allUiState, sortAndFilterState) {
        AllTabControllerPanelUiState(
            filterIcon = if (sortAndFilterState.filter.size == 1) {
                sortAndFilterState.filter.first().icon
            } else {
                R.drawable.filter
            },
            orderByIcon = when (sortAndFilterState.orderBy) {
                OrderBy.ASCENDING -> R.drawable.ascending_sort
                OrderBy.DESCENDING -> R.drawable.descending_sorting
            },
            sortByIcon = when (sortAndFilterState.sortBy) {
                SortBy.TIME -> R.drawable.time
                SortBy.AMOUNT -> R.drawable.outline_amount
                SortBy.LABEL -> R.drawable.outline_label
                SortBy.FULFILLED -> R.drawable.ic_refund
            },
            periodTypeIcon = when (allUiState.periodType) {
                PeriodType.WEEK -> R.drawable.ic_week
                PeriodType.MONTH -> R.drawable.ic_month
                PeriodType.YEAR -> R.drawable.ic_year
                PeriodType.DAY -> R.drawable.ic_day
            },
            transactionListSortName = when (allUiState.isTransactionListSort) {
                OrderBy.ASCENDING -> "Oldest"
                OrderBy.DESCENDING -> "Latest"
            },
            transactionListSortIcon = when (allUiState.isTransactionListSort) {
                OrderBy.DESCENDING -> R.drawable.ic_sort_latest
                OrderBy.ASCENDING -> R.drawable.ic_sort_oldest
            },
            filterSelected = sortAndFilterState.filter != listOf(Filter.ALL),
            orderBySelected = sortAndFilterState.orderBy != OrderBy.DESCENDING,
            sortBySelected = sortAndFilterState.sortBy != SortBy.TIME,
            periodTypeSelected = allUiState.periodType != PeriodType.WEEK,
            isTransactionListSortSelected = allUiState.isTransactionListSort != OrderBy.DESCENDING,
            isSearchExpanded = allUiState.isSearchExpanded,
            isRecentSearchExpanded = allUiState.isRecentSearchExpanded
        )
    }

    val recentSearchIcon = if (state.isRecentSearchExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown


    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (state.isSearchExpanded) {
            IconButton(
                onClick = {
                    onShowRecentSearch(!state.isRecentSearchExpanded)
                }
            ) {
                Icon(
                    imageVector = recentSearchIcon,
                    contentDescription = "show recent",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        AnimatedContent(
            targetState = state.isRecentSearchExpanded,
            transitionSpec = {
                fadeIn() togetherWith fadeOut()
            },
            label = "Recent Search",
        ) { expanded ->

            if (!expanded) {


                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    item(key = "Transaction list sort") {
                        AllTabListPanelButton(
                            text = state.transactionListSortName,
                            isSelect = state.isTransactionListSortSelected,
                            icon = state.transactionListSortIcon,
                            onClick = onTransactionListSort
                        )
                    }

                    item(key = "Period Type") {
                        AllTabListPanelButton(
                            text = allUiState.periodType.name.lowercase()
                                .replaceFirstChar { it.uppercase() },
                            isSelect = state.periodTypeSelected,
                            icon = state.periodTypeIcon,
                            onClick = onPeriodTypeClick
                        )
                    }

                    item(key = "Order By") {
                        AllTabListPanelButton(
                            text = "Order",
                            isSelect = state.orderBySelected,
                            icon = state.orderByIcon,
                            onClick = onOrderByClick
                        )
                    }

                    item(key = "Sort By") {
                        AllTabListPanelButton(
                            text = "Sort",
                            isSelect = state.sortBySelected,
                            icon = state.sortByIcon,
                            onClick = onSortByClick
                        )
                    }
                    item(key = "Filter") {
                        AllTabListPanelButton(
                            text = "Filter",
                            isSelect = state.filterSelected,
                            icon = state.filterIcon,
                            onClick = onFilterClick
                        )
                    }
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LazyRow(
                        modifier = Modifier
                            .fillMaxWidth(0.9f),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        stickyHeader(key = "Recent Searches") {
                            Text(
                                text = "Recent Searches",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(vertical = 5.dp, horizontal = 8.dp)
                            )
                        }

                        items(recentSearches.size, key = { recentSearches[it] }) { index ->
                            val text = recentSearches[index]
                            Button(
                                onClick = {
                                    allUiState.search.clearText()
                                    allUiState.search.setTextAndPlaceCursorAtEnd(text)
                                },
                                contentPadding = PaddingValues(vertical = 1.dp, horizontal = 1.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ExtendedTheme.colors.lightGray,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = MaterialTheme.shapes.small,
                                modifier = Modifier
                                    .height(35.dp)
                                    .padding(vertical = 2.dp)
                            ) {
                                Text(
                                    text = text,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    IconButton(
                        onClick = onClearRecentSearch
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear recent searches",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AllTabListPanelButton(
    text: String,
    icon: Int,
    isSelect: Boolean = false,
    onClick: () -> Unit
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedColor = remember(
        isSelect,
    ) { if (isSelect) primaryColor.copy(alpha = 0.2f) else Color.Transparent }

    val iconColor = remember(
        isSelect
    ) {
        if (isSelect) primaryColor else surfaceColor
    }

    val borderColor = remember(
        isSelect,
    ) { if (isSelect) primaryColor else null }

    val border = if (borderColor != null) {
        BorderStroke(
            width = 1.dp,
            color = borderColor
        )
    } else {
        null
    }

    OutlinedButton(
        onClick = onClick,
        colors = ButtonDefaults
            .outlinedButtonColors()
            .copy(
                containerColor = selectedColor,
            ),
        border = border,
        shape = MaterialTheme.shapes.medium,
        contentPadding = PaddingValues(vertical = 2.dp, horizontal = 8.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = iconColor
            )
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Bold
            )
        }
    }
}