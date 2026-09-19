// Grace and truth came through JESUS
package com.den.steward.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarViewWeek
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.den.steward.R
import com.den.steward.backend.states.PeriodType
import com.den.steward.backend.useCase.Filter
import com.den.steward.backend.useCase.OrderBy
import com.den.steward.backend.useCase.SortBy
import com.den.steward.helper.title
import com.den.steward.ui.theme.ExtendedTheme


private val ICON_SIZE = 24.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SortFilterBottomSheetLayout(
    title: String,
    icon: @Composable () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    val state = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        sheetState = state,
        dragHandle = { },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(0.9f),
                shape = MaterialTheme.shapes.medium
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.Start,
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier.weight(1f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            icon()

                            Spacer(
                                modifier = Modifier.width(10.dp)
                            )

                            Text(
                                title,
                                style = MaterialTheme.typography.titleMedium,
                            )
                        }

                        FilledIconButton (
                            onClick = onDismiss,
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = ExtendedTheme.colors.lightGray,
                                contentColor = MaterialTheme.colorScheme.onSurface
                            )
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = "Close Icon",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }

                    }

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 10.dp)
                    )

                    content()
                }
            }
        }
    }
}

@Composable
fun FilterBottomSheet(
    isExpanded: Boolean,
    selected: Filter,
    onFilterSelected: (Filter) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isExpanded) {
        FilterBottomSheetContent(
            selected = selected,
            onFilterSelected = onFilterSelected,
            onDismiss = onDismiss
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterBottomSheetContent(
    selected: Filter,
    onFilterSelected: (Filter) -> Unit, onDismiss: () -> Unit
) {
    val entries = Filter.entries

    SortFilterBottomSheetLayout(
        title = "Transaction Filter",
        icon = {
            Image(
                painter = painterResource(id = R.drawable.filter),
                contentDescription = "Filter Icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface)
            )
        },
        onDismiss = onDismiss
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        ) {
            items(
                count = entries.size,
                key = { index -> entries[index].name }
            ) { index ->
                val entry = entries[index]
                FilterBottomSheetItem(
                    filter = entry,
                    selected = selected == entry,
                    onFilterSelected = onFilterSelected
                )
            }
        }
    }
}

@Composable
fun FilterBottomSheetItem(
    filter: Filter,
    selected: Boolean = false,
    onFilterSelected: (Filter) -> Unit
) {
    val icon = when (filter) {
        Filter.ALL -> R.drawable.filter
        Filter.EARNINGS -> R.drawable.ic_earnings
        Filter.EXPENSE -> R.drawable.ic_expense
        Filter.GOAL -> R.drawable.ic_finance_target
        Filter.SAVINGS -> R.drawable.ic_savings
        Filter.REPAYMENT -> R.drawable.ic_repayment
        Filter.SETTLEMENT -> R.drawable.ic_refund
        Filter.ATTAIN -> R.drawable.ic_attain
        Filter.LENT -> R.drawable.ic_loan
        Filter.DEBT -> R.drawable.ic_debt
        Filter.PLAN -> R.drawable.ic_plan
    }

    val desc = when (filter) {
        Filter.ALL -> "View all your financial activities"
        Filter.EXPENSE -> "Track your daily spending and costs"
        Filter.EARNINGS -> "Monitor your income and revenue streams"
        Filter.GOAL -> "Manage your long-term financial targets"
        Filter.SAVINGS -> "View your savings and reserve funds"
        Filter.REPAYMENT -> "Track payments made towards loans"
        Filter.SETTLEMENT -> "Monitor returned settlement and reversals"
        Filter.ATTAIN -> "Track progress towards your goals"
        Filter.LENT -> "Manage money lent out to others"
        Filter.DEBT -> "Track your outstanding liabilities"
        Filter.PLAN -> "Plan for your future financial targets"
    }

    val filterName = remember(filter) {
        if (filter == Filter.ALL) "All Activities" else filter.name.title
    }
    val primaryColor = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedColor = remember(selected) {
        if (selected) primaryColor else onSurfaceColor
    }

        Row(
            Modifier
                .fillMaxWidth()
                .height(56.dp)
                .selectable(
                    selected = selected,
                    onClick = { onFilterSelected(filter) },
                    role = Role.RadioButton,
                )
                .padding(horizontal = 3.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start
            ) {
                Image(
                    painter = painterResource(id = icon),
                    contentDescription = "$filterName filter icon",
                    colorFilter = ColorFilter.tint(selectedColor),
                    modifier = Modifier.size(ICON_SIZE)
                )

                Spacer(
                    modifier = Modifier.width(10.dp)
                )


                Column {
                    Text(
                        filterName,
                        style = MaterialTheme.typography.titleMedium,
                        color = selectedColor
                    )

                    Text(
                        desc,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }

}


@Composable
fun OrderByBottomSheet(
    isExpanded: Boolean,
    selected: OrderBy,
    onSortSelected: (OrderBy) -> Unit,
    onDismiss: () -> Unit,
) {
    if (isExpanded) {
        OrderByBottomSheetContent(
            selected = selected,
            onSortSelected = onSortSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
private fun OrderByBottomSheetContent(selected: OrderBy, onSortSelected: (OrderBy) -> Unit, onDismiss: () -> Unit) {
    val entries = OrderBy.entries

    SortFilterBottomSheetLayout(
        title = "Transaction Order",
        icon = {
            Image(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = "OrderBy Icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),

            )
        },
        onDismiss = onDismiss
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
        ) {
            entries.forEach { entry ->
                OrderByBottomSheetItem(
                    orderBy = entry,
                    selected = selected == entry,
                    onSortSelected = onSortSelected
                )
            }
        }
    }
}

@Composable
private fun OrderByBottomSheetItem(orderBy: OrderBy, selected: Boolean, onSortSelected: (OrderBy) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedTextColor = remember(selected) {
        if (selected) primary else onSurfaceColor
    }

    val icon = when (orderBy) {
        OrderBy.ASCENDING -> R.drawable.ascending_sort
        OrderBy.DESCENDING -> R.drawable.descending_sorting
    }

    val desc = when (orderBy) {
        OrderBy.ASCENDING -> "Order by ascending order"
        OrderBy.DESCENDING -> "Order by descending order"
    }

    val name = remember(orderBy) {
        orderBy.name.title
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onSortSelected(orderBy) },
                role = Role.RadioButton
            )
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "$name filter icon",
            colorFilter = ColorFilter.tint(selectedTextColor),
            modifier = Modifier.size(ICON_SIZE)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium,
                color = selectedTextColor
            )

            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
fun SortByBottomSheet(
    isExpanded: Boolean,
    selected: SortBy,
    onSortSelected: (SortBy) -> Unit,
    onDismiss: () -> Unit,
){
    if (isExpanded) {
        SortByBottomSheetContent(
            selected = selected,
            onSortSelected = onSortSelected,
            onDismiss = onDismiss
        )
    }
}

@Composable
fun SortByBottomSheetContent(
    selected: SortBy,
    onSortSelected: (SortBy) -> Unit,
    onDismiss: () -> Unit
) {
    val entries = SortBy.entries
    SortFilterBottomSheetLayout(
        title = "Transaction Sort",
        icon = {
            Image(
                painter = painterResource(id = R.drawable.sort),
                contentDescription = "OrderBy Icon",
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onSurface),
            )
        },
        onDismiss = onDismiss
    ) {
        LazyColumn (
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
            ) {
            items(
                count = entries.size,
                key = { index -> entries[index] }
            ) { index ->
                val entry = entries[index]

                SortByBottomSheetItem(
                    sortBy = entry,
                    selected = selected == entry,
                    onSortSelected = onSortSelected
                )
            }
        }
    }
}

@Composable
fun SortByBottomSheetItem(sortBy: SortBy, selected: Boolean, onSortSelected: (SortBy) -> Unit) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedTextColor = remember(selected) {
        if (selected) primary else onSurfaceColor
    }

    val icon = when (sortBy) {
        SortBy.TIME -> R.drawable.time
        SortBy.AMOUNT -> R.drawable.outline_amount
        SortBy.LABEL -> R.drawable.outline_label
        SortBy.FULFILLED -> R.drawable.ic_refund
    }

    val desc = when (sortBy) {
        SortBy.TIME -> "Sort by date"
        SortBy.AMOUNT -> "Sort by amount"
        SortBy.LABEL -> "Sort by name"
        SortBy.FULFILLED -> "Sort by fulfilled"
    }

    val name = remember(sortBy) {
        sortBy.name.title
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onSortSelected(sortBy) },
                role = Role.RadioButton
            )
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = "$name filter icon",
            colorFilter = ColorFilter.tint(selectedTextColor),
            modifier = Modifier.size(ICON_SIZE)
        )

        Spacer(
            modifier = Modifier.width(10.dp)
        )

        Column {
            Text(
                name,
                style = MaterialTheme.typography.titleMedium,
                color = selectedTextColor
            )

            Text(
                desc,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}


@Composable
fun PeriodTypeBottomSelector(
    isExpanded: Boolean,
    currentPeriodType: PeriodType,
    onDismiss: () -> Unit,
    onPeriodTypeChange: (PeriodType) -> Unit
) {
    if (isExpanded) {
        SortFilterBottomSheetLayout(
            title = "Transaction Period",
            icon = {
                Icon(
                    imageVector = Icons.Default.CalendarViewWeek,
                    contentDescription = "Period Type Icon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            },
            onDismiss = onDismiss
        ) {
            PeriodType.entries.forEach {
                PeriodTypeBottomSheetItem(
                    periodType = it,
                    selected = currentPeriodType == it,
                    onPeriodTypeChange = { periodType ->
                        onPeriodTypeChange(periodType)
                        onDismiss()
                    }
                )
            }
        }
    }
}

@Composable
fun PeriodTypeBottomSheetItem(
    periodType: PeriodType,
    selected: Boolean,
    onPeriodTypeChange: (PeriodType) -> Unit
) {
    val primary = MaterialTheme.colorScheme.primary
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface

    val selectedTextColor = remember(selected) {
        if (selected) primary else onSurfaceColor
    }

    val name = remember(periodType) {
        periodType.name.title
    }

    val desc = when (periodType) {
        PeriodType.DAY -> "View transactions for today"
        PeriodType.WEEK -> "View transactions for this week"
        PeriodType.MONTH -> "View transactions for this month"
        PeriodType.YEAR -> "View transactions for this year"
    }

    val icon = when (periodType) {
        PeriodType.DAY -> R.drawable.ic_day
        PeriodType.WEEK -> R.drawable.ic_week
        PeriodType.MONTH -> R.drawable.ic_month
        PeriodType.YEAR -> R.drawable.ic_year
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .selectable(
                selected = selected,
                onClick = { onPeriodTypeChange(periodType) },
                role = Role.RadioButton
            )
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = "$name filter icon",
                colorFilter = ColorFilter.tint(selectedTextColor),
                modifier = Modifier.size(ICON_SIZE)
            )

            Spacer(
                modifier = Modifier.width(10.dp)
            )

            Column {
                Text(
                    name,
                    style = MaterialTheme.typography.titleMedium,
                    color = selectedTextColor
                )

                Text(
                    desc,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

}
