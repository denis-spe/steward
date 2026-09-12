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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.den.steward.backend.useCase.Filter
import com.den.steward.helper.title

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SortFilterBottomSheetLayout(
    title: String,
    icon: @Composable () -> Unit,
    onDismiss: () -> Unit,
    content: @Composable () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
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
                        .padding(10.dp),
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

                        IconButton(
                            onClick = onDismiss
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
                .fillMaxHeight(0.3f)
        ) {
            items(
                count = entries.size,
                key = { index -> entries[index] }
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
        Filter.REFUND -> R.drawable.ic_refund
        Filter.ATTAIN -> R.drawable.ic_attain
        Filter.LENT -> R.drawable.ic_loan
        Filter.DEBT -> R.drawable.ic_debt
    }

    val desc = when (filter) {
        Filter.ALL -> "View all your financial activities"
        Filter.EXPENSE -> "Track your daily spending and costs"
        Filter.EARNINGS -> "Monitor your income and revenue streams"
        Filter.GOAL -> "Manage your long-term financial targets"
        Filter.SAVINGS -> "View your savings and reserve funds"
        Filter.REPAYMENT -> "Track payments made towards loans"
        Filter.REFUND -> "Monitor returned funds and reversals"
        Filter.ATTAIN -> "Track progress towards your goals"
        Filter.LENT -> "Manage money lent out to others"
        Filter.DEBT -> "Track your outstanding liabilities"
    }

    val filterName = remember(filter) {
        if (filter == Filter.ALL) "All Activities" else filter.name.lowercase().title
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
                    colorFilter = ColorFilter.tint(selectedColor)
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

