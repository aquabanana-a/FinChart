@file:OptIn(ExperimentalLayoutApi::class)

package com.banana.finchart.ui.screen.main

import android.graphics.DashPathEffect
import android.view.View
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import com.banana.finchart.data.market.OHLC
import com.banana.finchart.ui.common.chart.modifier.SnapCrosshairModifier
import com.banana.finchart.ui.common.chart.VolumePaletteProvider
import com.banana.finchart.ui.common.chart.modifier.verticalCursor.VerticalCursorModifier
import com.banana.finchart.ui.common.util.FormatUtil.format
import com.banana.finchart.ui.common.util.FormatUtil.formatWithComma
import com.banana.finchart.ui.common.util.TouchUtil.overrideTouch
import com.banana.finchart.ui.theme.ChartGreenUp
import com.banana.finchart.ui.theme.ChartGridColor
import com.banana.finchart.ui.theme.ChartRedDown
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.model.dataSeries.XyDataSeries
import com.scichart.charting.modifiers.CursorModifier
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

    private lateinit var crosshairModifier: SnapCrosshairModifier

    @Composable
    fun TradingChart(modifier: Modifier, mainViewModel: MainScreenViewModel) {
        val crosshairState by mainViewModel.crosshairStateFlow.collectAsState()

        val chartSnapshot by mainViewModel.activeChartSnapshot.collectAsState()
        if (chartSnapshot == null) return

        val xAxisSharedVisibleRange = remember { mutableStateOf(
            DateRange(
                chartSnapshot!!.ohlcDataSet.first().timestampDate,
                chartSnapshot!!.ohlcDataSet.last().timestampDate
            ))
        }

        val cursorOhlc = remember(crosshairState.timestampMs) {
            if (crosshairState is SnapCrosshairModifier.State.None)
                null
            else
                chartSnapshot!!.ohlcDataSet.find { it.timestampMs == crosshairState.timestampMs }
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
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
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

        val bgModifier = Modifier
            .wrapContentSize()
            .background(
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
                val ohlcList = mainViewModel.activeChartSnapshot.value!!.ohlcDataSet

                val xAxis = DateAxis(surface.context).apply {
                    axisId = "xAxisCandle"
                    visibility = View.GONE
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
                    clear()
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

                crosshairModifier = SnapCrosshairModifier(
                    candleDataSeries = dataSeries,
                    stateFlow = mainViewModel.crosshairStateFlow,
                )

                UpdateSuspender.using(surface) {
                    applyAxisStyle(xAxis, yAxis)
                    applyCursorModifierStyle(crosshairModifier)

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
                val ohlcList = mainViewModel.activeChartSnapshot.value!!.ohlcDataSet

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
                    clear()
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

                val volumeCross = VerticalCursorModifier().apply {
                    crosshairModifier.setModifierTouchListener {
                        onTouch(it.overrideTouch(surface, this))
                    }
                }

                UpdateSuspender.using(surface) {
                    applyAxisStyle(xAxis, yAxis)
                    applyVolumeStyle(columnSeries, ohlcList)
                    applyCursorModifierStyle(volumeCross)

                    Collections.addAll(surface.renderableSeries, columnSeries)
                    Collections.addAll(
                        surface.chartModifiers,
                        volumeCross
                    )
                }
            }
        )
    }

    private fun applyCursorModifierStyle(modifier: CursorModifier) {
        modifier.apply {
            offset = 0f
            showTooltip = false
            crosshairPaint.apply {
                pathEffect = DashPathEffect(floatArrayOf(12f, 6f), 0f)
            }
        }
    }

    private fun applyVolumeStyle(columnSeries: FastColumnRenderableSeries, ohlcSet: Set<OHLC>) {
        columnSeries.paletteProvider = VolumePaletteProvider(
            ohlcSet.map { it.openPrice },
            ohlcSet.map { it.closePrice }).apply {
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
    fun LicenseKeyDialog(
        onConfirm: (String) -> Unit
    ) {
        var key by remember { mutableStateOf("") }

        AlertDialog(
            modifier = Modifier.imePadding(),
            onDismissRequest = { },
            title = {
                Text(
                    text = "Введите ключ продукта",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                TextField(
                    value = key,
                    onValueChange = { key = it },
                    singleLine = true,
                    placeholder = { Text("Ключ лицензии") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                        errorTextColor = MaterialTheme.colorScheme.error,
                        cursorColor = MaterialTheme.colorScheme.primary
                    )
                )
            },
            confirmButton = {
                Button(
                    onClick = { onConfirm(key) },
                    enabled = key.isNotBlank()
                ) {
                    Text("OK")
                }
            }
        )
    }
}