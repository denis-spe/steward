// Bless be the name of LORD GOD
package com.den.steward.ui.components.charts

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun ComposePieChart(
    data: MutableState<List<Pie>>
) {
    PieChart(
        modifier = Modifier.size(200.dp),
        data = data.value,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.value.indexOf(it)
            data.value =
                data.value.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
//        selectedScale = 1.2f,
//        scaleAnimEnterSpec = spring(
//            dampingRatio = Spring.DampingRatioMediumBouncy,
//            stiffness = Spring.StiffnessLow
//        ),
//        colorAnimEnterSpec = tween(300),
//        colorAnimExitSpec = tween(300),
//        scaleAnimExitSpec = tween(300),
//        spaceDegreeAnimExitSpec = tween(300),
        startDegree = -90f,
        spaceDegree = 7f,
        selectedPaddingDegree = 4f,
        style = Pie.Style.Stroke(width = 20.dp),
    )
}