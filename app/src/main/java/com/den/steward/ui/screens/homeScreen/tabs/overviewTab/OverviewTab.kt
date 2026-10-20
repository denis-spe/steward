package com.den.steward.ui.screens.homeScreen.tabs.overviewTab

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.viewModels.OverviewViewModel

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
@Composable
fun OverviewTab(
    padding: PaddingValues,
    overviewViewModel: OverviewViewModel = hiltViewModel(),
    backStack: NavBackStack<NavKey>
) {
    val overviewUiState by overviewViewModel.overviewUiState.collectAsStateWithLifecycle()
    val groupedTransactions by overviewViewModel.groupedTransactions.collectAsStateWithLifecycle()


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
    ) {
        OverviewList(
            dataState = groupedTransactions,
            backStack = backStack
        )
    }
}
