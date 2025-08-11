package com.banana.finchart.ui.common.chart

import android.graphics.Color
import com.scichart.charting.visuals.renderableSeries.XyRenderableSeriesBase
import com.scichart.charting.visuals.renderableSeries.data.XyRenderPassData
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IFillPaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.IStrokePaletteProvider
import com.scichart.charting.visuals.renderableSeries.paletteProviders.PaletteProviderBase
import com.scichart.core.model.IntegerValues

class VolumePaletteProvider(
    private val openPrices: List<Double>,
    private val closePrices: List<Double>
) : PaletteProviderBase<XyRenderableSeriesBase>(XyRenderableSeriesBase::class.java),
    IFillPaletteProvider, IStrokePaletteProvider {

    var upColor = Color.GREEN
    var downColor = Color.RED

    private val fillColors = IntegerValues()
    private val strokeColors = IntegerValues()

    override fun update() {
        val rpd = renderableSeries?.currentRenderPassData as? XyRenderPassData ?: return

        val pointCount = rpd.pointsCount()
        fillColors.setSize(pointCount)
        strokeColors.setSize(pointCount)

        val indices = rpd.indices

        for (i in 0 until pointCount) {
            val globalIndex = indices[i]

            val open = openPrices[globalIndex]
            val close = closePrices[globalIndex]

            val color = if (close >= open) upColor else downColor

            fillColors.set(i, color)
            strokeColors.set(i, color)
        }
    }

    override fun getFillColors(): IntegerValues = fillColors

    override fun getStrokeColors(): IntegerValues = strokeColors
}