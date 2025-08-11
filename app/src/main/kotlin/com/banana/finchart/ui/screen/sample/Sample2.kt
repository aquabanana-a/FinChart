package com.banana.finchart.ui.screen.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.modifiers.ZoomPanModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.IAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.IRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.drawing.common.SolidBrushStyle
import java.util.Collections

object Sample2 {
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

            val lineDataSeries =
                XyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType)

            val scatterDataSeries =
                XyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType)

            for (i in 0..999) {
                lineDataSeries.append(i, Math.sin(i * 0.1))
                scatterDataSeries.append(i, Math.cos(i * 0.1))
            }

            val lineSeries: IRenderableSeries = FastLineRenderableSeries()
            lineSeries.dataSeries = lineDataSeries

            val pointMarker = EllipsePointMarker()
            pointMarker.fillStyle = SolidBrushStyle(-0xcd32ce)
            pointMarker.setSize(10, 10)

            val scatterSeries: IRenderableSeries = XyScatterRenderableSeries()
            scatterSeries.dataSeries = scatterDataSeries
            scatterSeries.pointMarker = pointMarker

            UpdateSuspender.using(surface) {
                Collections.addAll(surface.renderableSeries, lineSeries, scatterSeries)
                Collections.addAll(
                    surface.chartModifiers,
                    PinchZoomModifier(),
                    ZoomPanModifier(),
                    ZoomExtentsModifier()
                )
            }
        })
    }
}