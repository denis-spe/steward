package com.den.steward.ui.screens.homeScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.den.steward.backend.viewModels.DataAdditionViewModel
import com.den.steward.backend.viewModels.DataFetchViewModel
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.ui.dataAddition.AddTransactionFloatingActionButton
import com.den.steward.ui.screens.homeScreen.transactionCharts.TransactionDonutChart
import com.den.steward.ui.screens.homeScreen.transactionList.FinancialPeriodList

@Composable
fun HomeScreen(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel,
    dataAdditionViewModel: DataAdditionViewModel,
    dataFetchViewModel: DataFetchViewModel
) {

    val todayTransactions by dataFetchViewModel.todayTransactions.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        floatingActionButton = {

        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color.Transparent
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.clip(RoundedCornerShape(40.dp)),
                        shadowElevation = 4.dp,
                        tonalElevation = 2.dp
                    ) {
                        Row (
                            modifier = Modifier
                                .fillMaxWidth(0.7f)
                                .padding(horizontal = 6.dp, vertical = 3.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Apps,
                                    contentDescription = "Today"
                                )
                            }
                            AddTransactionFloatingActionButton(
                                dataAdditionViewModel = dataAdditionViewModel
                            )

                            IconButton(
                                onClick = {}
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Today,
                                    contentDescription = "Today"
                                )
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier.fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            TransactionDonutChart(viewModel = homeViewModel)

            FinancialPeriodList(
                modifier = Modifier.weight(1f),
                transactions = todayTransactions
            )
        }
    }
}