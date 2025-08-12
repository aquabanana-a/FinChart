package com.banana.finchart.ui.common.chart.modifier.verticalCursor

import android.graphics.PointF
import com.scichart.charting.modifiers.IChartModifier
import com.scichart.charting.modifiers.behaviors.AxisTooltipsBehavior
import com.scichart.charting.visuals.axes.IAxisTooltip

class VerticalCursorTooltipsBehavior<T : IChartModifier>(modifierType: Class<T>) :
    AxisTooltipsBehavior<T>(modifierType) {

    override fun updateYAxisTooltip(tooltip: IAxisTooltip, point: PointF) {
        // stub
    }
}