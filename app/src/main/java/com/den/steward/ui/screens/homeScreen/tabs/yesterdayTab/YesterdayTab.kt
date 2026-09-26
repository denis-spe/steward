// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.yesterdayTab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.viewModels.DataDeletionViewModel
import com.den.steward.backend.viewModels.YesterdayViewModel
import com.den.steward.ui.components.FilterBottomSheet
import com.den.steward.ui.components.OrderByBottomSheet
import com.den.steward.ui.components.SortByBottomSheet

@Composable
fun YesterdayTab(
    padding: PaddingValues,
    yesterdayViewModel: YesterdayViewModel = hiltViewModel(),
    dataDeletionViewModel: DataDeletionViewModel = hiltViewModel()
) {

    val sortAndFilterState by yesterdayViewModel.sortAndFilterState.collectAsStateWithLifecycle()
    val yesterdayTabState by yesterdayViewModel.yesterdayTabDataState.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier.fillMaxSize()
            .padding(padding)
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        YesterdayTabList(
            modifier = Modifier.fillMaxSize(),
            dataDeletionViewModel = dataDeletionViewModel,
            yesterdayTabState = yesterdayTabState,
            sortAndFilterState = sortAndFilterState,
            updateIsSortByExpanded = yesterdayViewModel::updateIsSortByExpanded,
            updateIsFilterExpanded = yesterdayViewModel::updateIsFilterExpanded,
            updateIsOrderExpanded = yesterdayViewModel::updateIsOrderExpanded
        )
    }

    FilterBottomSheet(
        isExpanded = sortAndFilterState.isFilterExpanded,
        selected = sortAndFilterState.filter,
        onFilterSelected = yesterdayViewModel::updateFilter,
        onDismiss = {
            yesterdayViewModel.updateIsFilterExpanded(false)
        }
    )

    OrderByBottomSheet(
        isExpanded = sortAndFilterState.isOrderByExpanded,
        selected = sortAndFilterState.orderBy,
        onSortSelected = yesterdayViewModel::updateOrderBy,
        onDismiss = {
            yesterdayViewModel.updateIsOrderExpanded(false)
        }
    )

    SortByBottomSheet(
        isExpanded = sortAndFilterState.isSortByExpanded,
        selected = sortAndFilterState.sortBy,
        onSortSelected = yesterdayViewModel::updateSortBy,
        onDismiss = {
            yesterdayViewModel.updateIsSortByExpanded(false)
        }
    )
}
