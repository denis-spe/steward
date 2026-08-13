package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.R
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.ui.dataAddition.TransactionAddition
import com.den.steward.ui.screens.homeScreen.transactionList.FinancialPeriodList
import com.den.steward.ui.screens.screenManager.SettingsRouter

@Composable
fun HomeScreen(backStack: NavBackStack<NavKey>, homeViewModel: HomeViewModel) {
    val todayTransactions by homeViewModel.todayTransactions.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {
            TransactionAddition(homeViewModel::transactionDataSubmission)
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                modifier = Modifier.testTag(stringResource(R.string.home_screen_settings_button)),
                onClick = {
                    backStack.add(SettingsRouter)
                }
            ) {
                Text(stringResource(R.string.home_screen_settings_button))
            }

            Button(
                modifier = Modifier,
                onClick = {
                }
            ) {
                Text("Add data")
            }

            FinancialPeriodList(
                modifier = Modifier.weight(1f),
                transactions = todayTransactions
            )

        }
    }
}