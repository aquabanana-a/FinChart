@file:OptIn(ExperimentalLayoutApi::class)

package com.banana.finchart.ui.screen.main

import android.graphics.DashPathEffect
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.banana.finchart.R
import com.banana.finchart.data.OHLC
import com.banana.finchart.ui.common.chart.SnapCrosshairModifier
import com.banana.finchart.ui.common.chart.VolumePaletteProvider
import com.banana.finchart.ui.common.util.FormatUtil.format
import com.banana.finchart.ui.common.util.FormatUtil.formatWithComma
import com.banana.finchart.ui.theme.ChartGreenUp
import com.banana.finchart.ui.theme.ChartGridColor
import com.banana.finchart.ui.theme.ChartRedDown
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.PinchZoomModifier
import com.scichart.charting.modifiers.ZoomExtentsModifier
import com.scichart.charting.visuals.SciChartSurface
import com.scichart.charting.visuals.axes.AxisBase
import com.scichart.charting.visuals.axes.DateAxis
import com.scichart.charting.visuals.axes.NumericAxis
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries
import com.scichart.charting.visuals.renderableSeries.FastColumnRenderableSeries
import com.scichart.core.framework.UpdateSuspender
import com.scichart.data.model.DateRange
import com.scichart.drawing.common.SolidPenStyle
import java.util.Collections
import java.util.Date

object MainScreen {

    private val ohlcList = listOf(
        OHLC(1622505600000L, 10.0, 12.0, 9.5, 11.5, 1000),
        OHLC(1622592000000L, 11.0, 12.5, 10.5, 11.0, 1500),
        OHLC(1622678400000L, 10.5, 11.5, 9.8, 11.2, 1300),
        OHLC(1622764800000L, 12.0, 13.0, 11.0, 12.5, 1100),
        OHLC(1622851200000L, 11.5, 12.0, 10.8, 11.0, 1600),
        OHLC(1622937600000L, 11.2, 11.8, 10.9, 11.5, 1400),
        OHLC(1623024000000L, 11.4, 11.9, 11.0, 11.2, 1350),
        OHLC(1623110400000L, 11.1, 12.3, 10.7, 12.0, 1700),
        OHLC(1623196800000L, 11.8, 12.5, 11.4, 12.3, 1550),
        OHLC(1623283200000L, 12.2, 12.7, 11.8, 12.1, 1800),
        OHLC(1623369600000L, 12.0, 12.4, 11.5, 12.2, 1650),
        OHLC(1623456000000L, 12.5, 12.8, 12.0, 12.6, 1900),
        OHLC(1623542400000L, 12.3, 12.6, 12.0, 12.1, 1750),
        OHLC(1623628800000L, 12.0, 12.3, 11.6, 11.9, 1600),
        OHLC(1623715200000L, 11.7, 12.1, 11.4, 11.6, 1500),
        OHLC(1623801600000L, 11.5, 12.4, 11.3, 11.7, 1550),
        OHLC(1623888000000L, 11.8, 12.7, 11.5, 12.0, 1600),
        OHLC(1623974400000L, 12.1, 12.9, 11.8, 12.3, 1700),
        OHLC(1624060800000L, 11.9, 12.5, 11.6, 12.0, 1650),
        OHLC(1624147200000L, 12.3, 12.8, 11.9, 12.5, 1750),
        OHLC(1624233600000L, 12.5, 13.0, 12.0, 12.7, 1800),
        OHLC(1624320000000L, 12.7, 13.2, 12.2, 12.8, 1850),
        OHLC(1624406400000L, 12.4, 13.1, 12.0, 12.5, 1900),
        OHLC(1624492800000L, 12.6, 13.3, 12.1, 12.7, 1950),
        OHLC(1624579200000L, 12.8, 13.4, 12.3, 12.9, 2000)
    )

    @Composable
    fun TradingChart(modifier: Modifier, mainViewModel: MainScreenViewModel) {
        val crosshairState by mainViewModel.crosshairStateFlow.collectAsState()

        val xAxisSharedVisibleRange = remember { mutableStateOf(DateRange(
            ohlcList.first().timestampDate,
            ohlcList.last().timestampDate)) }

        val cursorOhlc = remember(crosshairState.timestampMs) {
            ohlcList.find { it.timestampMs == crosshairState.timestampMs }
        }

        Column(modifier = modifier) {
            Box(
                modifier = Modifier
                    .weight(3f)
                    .fillMaxWidth()
            ) {
                CandleStickChart(
                    modifier = Modifier.fillMaxSize(),
                    visibleRange = xAxisSharedVisibleRange,
                    mainViewModel = mainViewModel
                )

                if (cursorOhlc != null) {
                    CursorCandleInfos(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = 4.dp, end = 4.dp, bottom = 2.dp),
                        ohlc = cursorOhlc
                    )
                }
            }
            VolumeChart(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                visibleRange = xAxisSharedVisibleRange,
                mainViewModel = mainViewModel
            )
        }
    }

    @Composable
    private fun CursorCandleInfos(modifier: Modifier, ohlc: OHLC) {
        val isBull = ohlc.closePrice >= ohlc.openPrice
        val valueColor =
            if (isBull) Color(0xFF4CAF50) else Color(0xFFF44336)

        val change = ohlc.closePrice - ohlc.openPrice
        val changePercent = (change / ohlc.openPrice) * 100

        val bgModifier = Modifier.wrapContentSize().background(
            Color(0x09000000),
            RoundedCornerShape(4.dp)
        )

        FlowRow(
            modifier = modifier.padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            CandleInfoParam(
                modifier = bgModifier,
                title = "ОТКР",
                value = ohlc.openPrice.formatWithComma(4),
                valueColor = valueColor
            )

            CandleInfoParam(
                modifier = bgModifier,
                title = "МАКС",
                value = ohlc.highPrice.formatWithComma(4),
                valueColor = valueColor
            )

            CandleInfoParam(
                modifier = bgModifier,
                title = "МИН",
                value = ohlc.lowPrice.formatWithComma(4),
                valueColor = valueColor
            )

            CandleInfoParam(
                modifier = bgModifier,
                title = "ЗАКР",
                value = ohlc.closePrice.formatWithComma(4),
                valueColor = valueColor
            )

            CandleInfoParam(
                modifier = bgModifier,
                title = "",
                value = "${if (change >= 0) "+" else ""}${change.formatWithComma(4)} (${changePercent.format(2)}%)",
                valueColor = valueColor
            )
        }
    }

    @Composable
    private fun CandleInfoParam(modifier: Modifier, title: String, value:String, valueColor: Color) {
        Text(
            modifier = modifier,
            text = buildAnnotatedString {
                append(title)
                withStyle(SpanStyle(color = valueColor)) {
                    append(value)
                }
            },
        )
    }

    @Composable
    private fun CandleStickChart(
        modifier: Modifier,
        visibleRange: MutableState<DateRange>,
        mainViewModel: MainScreenViewModel
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                SciChartSurface(context).apply {
                    theme = R.style.ChartTheme
                }
            },
            update = { surface ->
                val xAxis = DateAxis(surface.context).apply {
                    axisId = "xAxisCandle"
                    //visibility = View.GONE
                    this.visibleRange = visibleRange.value
                    setVisibleRangeChangeListener { axus, oldRange, newRange, isAnimated ->
                        if (visibleRange.value != newRange) {
                            visibleRange.value = newRange as DateRange
                        }
                    }
                }
                val yAxis = NumericAxis(surface.context).apply {
                    axisId = "yAxisCandle"
                }

                UpdateSuspender.using(surface) {
                    surface.xAxes.add(xAxis)
                    surface.yAxes.add(yAxis)
                }

                val dataSeries = OhlcDataSeries(
                    Date::class.javaObjectType,
                    Double::class.javaObjectType
                ).apply {
                    for (ohlc in ohlcList) {
                        append(
                            ohlc.timestampDate, ohlc.openPrice, ohlc.highPrice, ohlc.lowPrice,
                            ohlc.closePrice
                        )
                    }
                }

                val candleSeries = FastCandlestickRenderableSeries().apply {
                    this.dataSeries = dataSeries
                    xAxisId = "xAxisCandle"
                    yAxisId = "yAxisCandle"
                }

                val crosshairModifier = SnapCrosshairModifier(
                    candleDataSeries = dataSeries,
                    stateFlow = mainViewModel.crosshairStateFlow,
                ).apply {
                    crosshairPaint.apply {
                        pathEffect = DashPathEffect(floatArrayOf(12f, 6f), 0f)
                    }
                }

                UpdateSuspender.using(surface) {
                    applyAxisStyle(xAxis, yAxis)

                    Collections.addAll(surface.renderableSeries, candleSeries)
                    Collections.addAll(
                        surface.chartModifiers,
                        crosshairModifier,
                        PinchZoomModifier(),
                        //ZoomPanModifier(),
                        ZoomExtentsModifier(),
                    )
                }
            }
        )
    }

    @Composable
    private fun VolumeChart(
        modifier: Modifier,
        visibleRange: MutableState<DateRange>,
        mainViewModel: MainScreenViewModel
    ) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                SciChartSurface(context).apply {
                    theme = R.style.ChartTheme
                }
            },
            update = { surface ->
                val xAxis = DateAxis(surface.context).apply {
                    axisId = "xAxisVolume"
                    this.visibleRange = visibleRange.value
                    setVisibleRangeChangeListener { axus, oldRange, newRange, isAnimated ->
                        if (visibleRange.value != newRange) {
                            visibleRange.value = newRange as DateRange
                        }
                    }
                }
                val yAxis = NumericAxis(surface.context).apply {
                    axisId = "yAxisVolume"
                }

                surface.xAxes.clear()
                surface.yAxes.clear()
                UpdateSuspender.using(surface) {
                    surface.xAxes.add(xAxis)
                    surface.yAxes.add(yAxis)
                }

                val volumeSeries = XyDataSeries(
                    Date::class.javaObjectType,
                    Double::class.javaObjectType
                ).apply {
                    for (ohlc in ohlcList) {
                        append(
                            ohlc.timestampDate, ohlc.volume.toDouble()
                        )
                    }
                }

                val columnSeries = FastColumnRenderableSeries().apply {
                    dataSeries = volumeSeries
                    xAxisId = "xAxisVolume"
                    yAxisId = "yAxisVolume"
                }

//                val volumeCross = CursorModifier().apply {
//                }

                UpdateSuspender.using(surface) {
                    applyAxisStyle(xAxis, yAxis)
                    applyVolumeStyle(columnSeries)

                    Collections.addAll(surface.renderableSeries, columnSeries)
                    Collections.addAll(
                        surface.chartModifiers,
//                        volumeCross
                    )
                }
            }
        )
    }

    private fun applyVolumeStyle(columnSeries: FastColumnRenderableSeries) {
        columnSeries.paletteProvider = VolumePaletteProvider(
            ohlcList.map { it.openPrice },
            ohlcList.map { it.closePrice }).apply {
            upColor = ChartGreenUp
            downColor = ChartRedDown
        }
    }

    private fun applyAxisStyle(xAxis: AxisBase<*>, yAxis: AxisBase<*>) {
        xAxis.textFormatting = "dd/MM"
        xAxis.cursorTextFormatting = "dd/MM/yyyy HH:mm"
        xAxis.majorGridLineStyle = SolidPenStyle(ChartGridColor, true, 1f, null)
        xAxis.minorGridLineStyle = SolidPenStyle(ChartGridColor, true, 0.5f, null)

        yAxis.textFormatting = "0.##"
        yAxis.cursorTextFormatting = "0.#####"
        yAxis.majorGridLineStyle = SolidPenStyle(ChartGridColor, true, 1f, null)
        yAxis.minorGridLineStyle = SolidPenStyle(ChartGridColor, true, 0.5f, null)
    }

    @Composable
    fun LicenseKeyDialog(onDismiss: () -> Unit) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("Введите ключ продукта") },
            text = {
                var key by remember { mutableStateOf("") }
                TextField(value = key, onValueChange = { key = it })
            },
            confirmButton = {
                Button(onClick = {
                    onDismiss()
                }) {
                    Text("OK")
                }
            }
        )
    }

}