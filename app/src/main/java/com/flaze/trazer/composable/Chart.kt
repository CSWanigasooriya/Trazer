package com.flaze.trazer.composable

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import ir.ehsannarmani.compose_charts.PieChart
import ir.ehsannarmani.compose_charts.models.Pie

@Composable
fun OverviewChart(modifier: Modifier) {
    var data by remember {
        mutableStateOf(
            listOf(
                Pie(
                    label = "Android", data = 20.0, color = Color.Red, selectedColor = Color.Green
                ),
                Pie(
                    label = "Windows", data = 45.0, color = Color.Cyan, selectedColor = Color.Blue
                ),
                Pie(
                    label = "Linux", data = 35.0, color = Color.Gray, selectedColor = Color.Yellow
                ),
            )
        )
    }

    PieChart(
        modifier = modifier.size(200.dp),
        data = data,
        onPieClick = {
            println("${it.label} Clicked")
            val pieIndex = data.indexOf(it)
            data = data.mapIndexed { mapIndex, pie -> pie.copy(selected = pieIndex == mapIndex) }
        },
        selectedScale = 1.2f,
        scaleAnimEnterSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow
        ),
        colorAnimEnterSpec = tween(300),
        colorAnimExitSpec = tween(300),
        scaleAnimExitSpec = tween(300),
        spaceDegreeAnimExitSpec = tween(300),
        style = Pie.Style.Fill
    )
}