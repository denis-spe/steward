// Glory be to the LORD of hosts
package com.den.steward.ui.screens.homeScreen.transactionCharts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.den.steward.backend.states.DataState
import com.den.steward.backend.viewModels.HomeViewModel
import com.den.steward.helper.formatToAmount
import com.den.steward.ui.components.charts.DonutChart
import com.den.steward.ui.components.charts.DonutChartData
import com.den.steward.ui.components.charts.collections.DonutChartDataCollection

@Composable
fun TransactionDonutChart(
    viewModel: HomeViewModel
) {
    val donutChartState by viewModel.donutChart.collectAsStateWithLifecycle()
    val donutChartCenterAmount by viewModel.donutChartCenterAmount.collectAsStateWithLifecycle()

    when(donutChartState) {
        is DataState.Success -> {
            val donutChartData = (donutChartState as DataState.Success).data
            if (donutChartData.isEmpty()) {
                TransactionDonutChartEmptyView()
            } else {
                TransactionDonutChartView(
                    donutChartData = donutChartData,
                    donutChartCenterAmount = donutChartCenterAmount
                )
            }
        }

        is DataState.Error -> {
            val message = (donutChartState as DataState.Error).message
            TransactionDonutChartErrorView(message = message)
        }

        is DataState.Loading -> {
            TransactionDonutChartShimmerView()
        }
    }
}

@Composable
fun TransactionDonutChartView(
    donutChartData: List<DonutChartData>,
    donutChartCenterAmount: Double,
) {
    DonutChart(
        data = DonutChartDataCollection(donutChartData)
    ) { selectedItem ->
        if (selectedItem == null) {
            Text(
                text = donutChartCenterAmount.formatToAmount(),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis,
            )
        } else {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = selectedItem.title
                )

                Text(
                    text = selectedItem.amount.formatToAmount()
                )
            }
        }
    }
}

@Composable
fun TransactionDonutChartErrorView(
    message: String
) {

}

@Composable
fun TransactionDonutChartShimmerView() {

}

@Composable
fun TransactionDonutChartEmptyView() {

}