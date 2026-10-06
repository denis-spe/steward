// Grace and truth came through JESUS CHRIST
package com.den.steward.ui.screens.homeScreen.tabs.allTab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.useCase.PeriodDataHandleUseCase
import com.den.steward.backend.viewModels.AllViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import kotlinx.coroutines.launch

@Composable
fun AllTab(
    padding: PaddingValues,
    allViewModel: AllViewModel = hiltViewModel(),
    homeViewModel: HomeViewModel
) {
    val pagerState = rememberPagerState(
        initialPage = PeriodDataHandleUseCase.INITIAL_PAGE,
        pageCount = { Int.MAX_VALUE }
    )


    val allUiState by allViewModel.allUiState.collectAsStateWithLifecycle()
    val sortAndFilterState by allViewModel.sortAndFilterState.collectAsStateWithLifecycle()
    val allTabDataState by allViewModel.allTabDataState.collectAsStateWithLifecycle()
    val transactions by allViewModel.transactions.collectAsStateWithLifecycle()
    val homeUiState by homeViewModel.homeUiState.collectAsStateWithLifecycle()
    val recentSearches by allViewModel.recentSearches.collectAsStateWithLifecycle()
    val dataUpdateState by allViewModel.dataUpdateState.collectAsStateWithLifecycle()

    LaunchedEffect(homeUiState.allTabFilter) {
        homeUiState.allTabFilter?.let {
            allViewModel.updateFilter(it)
            homeViewModel.clearAllTabFilter()
        }
    }

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .collect { page ->
                allViewModel.onPageChange(page)
            }
    }

    val settledPage = pagerState.settledPage
    val weekDaysForPage = remember(settledPage) { allViewModel.getWeekDaysForPage(settledPage) }
    val transactionCountsState by remember(settledPage) {
        allViewModel.transactionCounts(settledPage)
    }.collectAsStateWithLifecycle()
    val transactionCountsByDate by allViewModel.transactionCountsByDate.collectAsStateWithLifecycle()



    val coroutineScope = rememberCoroutineScope()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // --- Header Section ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            WeekView(
                pagerState = pagerState,
                weekDaysForPage = weekDaysForPage,
                selectedDate = allUiState.selectedDate,
                weekNumber = allUiState.weekNumber,
                counts = transactionCountsState,
                periodType = allUiState.periodType,
                moreInfo = { day ->
                    val (flow, count) = transactionCountsByDate
                    val dayCount = count[day.date]
                    val dayFlow = flow[day.date]
                    val color = if (dayFlow == -1)
                        MaterialTheme.colorScheme.error
                    else Color(0xFF3BA429)


                    if (dayCount != null) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(2.dp),
                            colors = CardDefaults.cardColors().copy(
                                containerColor = color.copy(0.3f).compositeOver(
                                    MaterialTheme.colorScheme.background
                                )
                            )
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(2.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    dayCount.toString(),
                                    style = MaterialTheme.typography.bodySmall,
                                    color = color
                                )
                            }
                        }
                    }
                },
                onResetClick = {
                    coroutineScope.launch {
                        pagerState.animateScrollToPage(PeriodDataHandleUseCase.INITIAL_PAGE)
                    }
                },
                saveSearchQuery = allViewModel::saveSearchQuery,
                onShowRecentSearch = allViewModel::updateIsRecentSearchExpanded,
                isSearchExpanded = allUiState.isSearchExpanded,
                searchState = allUiState.search,
                onSearchShow = allViewModel::updateIsSearchExpanded
            ) {
                allViewModel.updateSelectedDate(it)
            }


            AllTabControllerPanel(
                allUiState = allUiState,
                sortAndFilterState = sortAndFilterState,
                recentSearches = recentSearches,
                onShowRecentSearch = allViewModel::updateIsRecentSearchExpanded,
                onClearRecentSearch = allViewModel::clearRecentSearches,
                onFilterClick = { allViewModel.updateIsFilterExpanded(true) },
                onSortByClick = { allViewModel.updateIsSortByExpanded(true) },
                onOrderByClick = { allViewModel.updateIsOrderByExpanded(true) },
                onPeriodTypeClick = { allViewModel.updateIsPeriodTypeExpanded(true) },
                onTransactionListSort = { allViewModel.updateIsTransactionListOrderExpanded(isExpanded = true) }
            )
        }

        HorizontalDivider(
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.outlineVariant
        )

        AllTabLazyList(
            sortAndFilterState = sortAndFilterState,
            allTabDataState = allTabDataState,
            transactions = transactions,
            allUiState = allUiState,
            dataUpdateState = dataUpdateState,
            allViewModel = allViewModel
        )
    }
}



