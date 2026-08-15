// Glory be to the LORD of hosts
package com.den.steward.ui.screens.homeScreen.transactionCharts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
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

    when(donutChartState) {
        is DataState.Success -> {
            val data = (donutChartState as DataState.Success).data
            TransactionDonutChartView(data = data)
        }

        is DataState.Error -> {
            val message = (donutChartState as DataState.Error).message
            TransactionDonutChartErrorView(message = message)
        }

        is DataState.Loading -> {
            TransactionDonutChartShimmerView()
        }

        is DataState.Empty -> {
            TransactionDonutChartEmptyView()
        }
    }
}

@Composable
fun TransactionDonutChartView(
    data: List<DonutChartData>
) {
    DonutChart(
        data = DonutChartDataCollection(data)
    ) { selectedItem ->
        if (selectedItem == null) {
            Text(
                text = data.sumOf { it.amount.toDouble() }.formatToAmount(),
                maxLines = 1,
                softWrap = false,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
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