package com.banana.finchart.ui.screen.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.core.framework.UpdateSuspender
import android.graphics.Color
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.*
import com.scichart.charting.visuals.annotations.HorizontalAnchorPoint
import com.scichart.charting.visuals.annotations.TextAnnotation
import com.scichart.charting.visuals.annotations.VerticalAnchorPoint
import com.scichart.charting.visuals.axes.AxisAlignment
import com.scichart.charting.visuals.pointmarkers.EllipsePointMarker
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries
import com.scichart.charting.visuals.renderableSeries.XyScatterRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.drawing.common.FontStyle
import com.scichart.drawing.common.SolidBrushStyle
import com.scichart.drawing.common.SolidPenStyle
import java.util.*
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import kotlin.math.cos
import kotlin.math.sin

object Sample7 {
    private val fifoCapacity = 300
    private val pointsCount = 200

    private val scheduledExecutorService = Executors.newSingleThreadScheduledExecutor()
    private lateinit var schedule: ScheduledFuture<*>

    private val lineDataSeries =
        XyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType).apply {
            seriesName = "Line Series"
            this.fifoCapacity = fifoCapacity
            setAcceptsUnsortedData(true)
        }

    private val scatterDataSeries =
        XyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType).apply {
            seriesName = "Scatter Series"
            this.fifoCapacity = fifoCapacity
            setAcceptsUnsortedData(true)
        }

    private val mountainDataSeries =
        XyDataSeries(Int::class.javaObjectType, Double::class.javaObjectType).apply {
            seriesName = "Mountain Series"
            this.fifoCapacity = fifoCapacity
            setAcceptsUnsortedData(true)
        }

    @Composable
    fun ChartView(modifier: Modifier) {
        var surface by remember { mutableStateOf<SciChartSurface?>(null) }
        var surface2 by remember { mutableStateOf<SciChartSurface?>(null) }
        var isInitialized by remember { mutableStateOf(false) }

        Column(modifier = modifier) {
            AndroidView(modifier = Modifier.weight(1f), factory = { context ->
                SciChartSurface(context).also { surface = it }
            }, update = { surface ->
            })

            AndroidView(modifier = Modifier.weight(1f), factory = { context ->
                SciChartSurface(context).also { surface2 = it }
            }, update = { surface ->
            })
        }

        LaunchedEffect(surface, surface2) {
            if (isInitialized || surface == null || surface2 == null)
                return@LaunchedEffect

            for (i in 0 until pointsCount) {
                lineDataSeries.append(i, sin(i * 0.1))
                scatterDataSeries.append(i, cos(i * 0.1))
                mountainDataSeries.append(i, cos(i * 0.1))
                count += 1
            }

            val lineSeries = FastLineRenderableSeries()
            lineSeries.yAxisId = "Primary Y-Axis"
            lineSeries.dataSeries = lineDataSeries

            val pointMarker = EllipsePointMarker()
            pointMarker.fillStyle = SolidBrushStyle(-0xcd32ce)
            pointMarker.setSize(10, 10)

            val scatterSeries = XyScatterRenderableSeries()
            scatterSeries.yAxisId = "Secondary Y-Axis"
            scatterSeries.dataSeries = scatterDataSeries
            scatterSeries.pointMarker = pointMarker

            UpdateSuspender.using(surface) {
                Collections.addAll(surface!!.renderableSeries, lineSeries, scatterSeries)
            }

            val mountainSeries = FastMountainRenderableSeries()
            mountainSeries.yAxisId = "Primary Y-Axis"
            mountainSeries.dataSeries = mountainDataSeries
            mountainSeries.strokeStyle = SolidPenStyle(0xFF0271B1.toInt(), false, 1.0f, null)
            mountainSeries.areaStyle = SolidBrushStyle(0xAAFF8D42.toInt())

            UpdateSuspender.using(surface2) {
                Collections.addAll(surface2!!.renderableSeries, mountainSeries)
            }

            setupSurface(surface!!)
            setupSurface(surface2!!)

            schedule = scheduledExecutorService.scheduleWithFixedDelay(
                { updateData(surface!!, surface2!!) },
                0,
                10,
                TimeUnit.MILLISECONDS
            )

            isInitialized = true
        }
    }

    private var count: Int = 0

    private val updateData: (SciChartSurface, SciChartSurface)->Unit = {
        surface, surface2 ->
        val x = count
        UpdateSuspender.using(surface) {
            lineDataSeries.append(x, sin(x * 0.1))
            scatterDataSeries.append(x, cos(x * 0.1))
            mountainDataSeries.append(x, cos(x * 0.1))

            tryAddAnnotationAt(surface, x)

            surface.zoomExtentsX()
            surface2.zoomExtentsX()
            count += 1
        }
    }

    private fun setupSurface(surface: SciChartSurface) {
        val yAxisRight = NumericAxis(surface.context)
        yAxisRight.axisTitle = "Primary Y-Axis"
        yAxisRight.axisId = "Primary Y-Axis"
        yAxisRight.axisAlignment = AxisAlignment.Right

        val yAxisLeft = NumericAxis(surface.context)
        yAxisLeft.axisTitle = "Secondary Y-Axis"
        yAxisLeft.axisId = "Secondary Y-Axis"
        yAxisLeft.axisAlignment = AxisAlignment.Left
        yAxisLeft.growBy = DoubleRange(0.2, 0.2)

        val rolloverModifier = RolloverModifier()
        rolloverModifier.receiveHandledEvents = true
        rolloverModifier.eventsGroupTag = "SharedEventGroup"

        UpdateSuspender.using(surface) {
            Collections.addAll(surface.xAxes, NumericAxis(surface.context))
            Collections.addAll(surface.yAxes, yAxisLeft, yAxisRight)
            Collections.addAll(
                surface.chartModifiers,
                ZoomExtentsModifier(),
                PinchZoomModifier(),
                rolloverModifier,
                XAxisDragModifier(),
                YAxisDragModifier()
            )
        }
    }

    /*
        // <SynchronizeVisibleRanges>
        // Create an IRange instance that will be shared across multiple charts
        val sharedXRange = DoubleRange()

        // Create an X axis and apply sharedXRange
        val xAxis = NumericAxis(this)
        xAxis.visibleRange = sharedXRange

        // Create another X axis and apply sharedXRange
        val xAxis2 = NumericAxis(this)
        xAxis2.visibleRange = sharedXRange
        // </SynchronizeVisibleRanges>
     */

    /*
        // <AddVerticalGroup>
        val verticalGroup = SciChartVerticalGroup()
        verticalGroup.addSurfaceToGroup(surface)
        verticalGroup.addSurfaceToGroup(surface2)
        // </AddVerticalGroup>
    */

    /*
        // <SetModifierGroup>
        val modifierGroup = ModifierGroup()
        modifierGroup.motionEventGroup = "SharedEventGroup"
        modifierGroup.receiveHandledEvents = true
        Collections.addAll(
            modifierGroup.childModifiers,
            ZoomExtentsModifier(),
            PinchZoomModifier(),
            rolloverModifier,
            XAxisDragModifier(),
            YAxisDragModifier()
        )
        // </SetModifierGroup>
     */

    private fun tryAddAnnotationAt(surface: SciChartSurface, x: Int) {
        if (x % 100 == 0) {
            val label = TextAnnotation(surface.context)
            label.yAxisId = if (x % 200 == 0) "Primary Y-Axis" else "Secondary Y-Axis"
            label.text = "N"
            label.x1 = x
            label.y1 = 0
            label.horizontalAnchorPoint = HorizontalAnchorPoint.Center
            label.verticalAnchorPoint = VerticalAnchorPoint.Center
            label.fontStyle = FontStyle(30f, Color.WHITE)

            surface.annotations.add(label)

            if (x > fifoCapacity) {
                surface.annotations.removeAt(0)
            }
        }
    }
}