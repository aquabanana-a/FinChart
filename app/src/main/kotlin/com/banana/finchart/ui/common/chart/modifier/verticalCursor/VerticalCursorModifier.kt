package com.banana.finchart.ui.common.chart.modifier.verticalCursor

import com.scichart.charting.R
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.behaviors.AxisTooltipsBehaviorBase
import com.scichart.charting.modifiers.behaviors.CursorCrosshairDrawableBehavior
import com.scichart.charting.modifiers.behaviors.CursorTooltipBehavior

class VerticalCursorModifier : CursorModifier {

    constructor() : this(R.layout.scichart_default_cursor_modifier_tooltip_container)

    constructor(tooltipContainerLayoutResId: Int) : this(
        CursorTooltipBehavior(VerticalCursorModifier::class.java, tooltipContainerLayoutResId),
        VerticalCursorTooltipsBehavior(VerticalCursorModifier::class.java),
        VerticalCursorDrawableBehavior(VerticalCursorModifier::class.java)
    )

    constructor(
        cursorTooltipBehavior: CursorTooltipBehavior<*>,
        axisTooltipsBehavior: AxisTooltipsBehaviorBase<*>,
        crosshairDrawableBehavior: CursorCrosshairDrawableBehavior<*>
    ) : super(cursorTooltipBehavior, axisTooltipsBehavior, crosshairDrawableBehavior)
}