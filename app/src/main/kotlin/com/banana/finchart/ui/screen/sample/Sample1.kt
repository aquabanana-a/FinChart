package com.banana.finchart.ui.screen.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.core.model.DoubleValues
import kotlin.math.sin

object Sample1 {
    @Composable
    fun ChartView(modifier: Modifier) {
        AndroidView(modifier = modifier, factory = { context ->
            SciChartSurface(context)
        }, update = { surface ->
            val xAxis: IAxis = NumericAxis(surface.context)
            val yAxis: IAxis = NumericAxis(surface.context)

            UpdateSuspender.using(surface) {
                surface.xAxes.add(xAxis)
                surface.yAxes.add(yAxis)
            }

            val count = 1000
            val xValues = DoubleValues(count)
            val yValues = DoubleValues(count)
            for (i in 0 until count) {
                val x = 10.0 * i.toDouble() / count.toDouble()
                val y = sin(2 * x)
                xValues.add(x)
                yValues.add(y)
            }

            val dataSeries =
                XyDataSeries(Double::class.javaObjectType, Double::class.javaObjectType).apply {
                    append(xValues, yValues)
                }

            val renderableSeries = FastLineRenderableSeries().apply {
                this.dataSeries = dataSeries
            }

            surface.renderableSeries.add(renderableSeries)
        })
    }
}