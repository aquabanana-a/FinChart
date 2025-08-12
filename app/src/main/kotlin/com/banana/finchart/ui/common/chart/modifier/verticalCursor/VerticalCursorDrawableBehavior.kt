package com.banana.finchart.ui.common.chart.modifier.verticalCursor

import android.graphics.Canvas
import android.graphics.Path
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.charting.modifiers.behaviors.CursorCrosshairDrawableBehavior

class VerticalCursorDrawableBehavior<T : CursorModifier>(modifierType: Class<T>) :
    CursorCrosshairDrawableBehavior<T>(modifierType) {

    private val path = Path()

    override fun onDrawOverlay(canvas: Canvas) {
        if (isLastPointValid) {
            val x = lastUpdatePoint.x
            val crosshairPaint = (modifier as CursorModifier).crosshairPaint

            path.moveTo(x, 0f)
            path.lineTo(x, canvas.height.toFloat())

            canvas.drawPath(path, crosshairPaint)
            path.rewind()
        }
    }
}