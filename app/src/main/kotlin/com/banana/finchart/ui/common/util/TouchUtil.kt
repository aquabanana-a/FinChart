package com.banana.finchart.ui.common.util

import android.os.SystemClock
import android.view.MotionEvent
import com.scichart.core.utility.touch.IPublishMotionEvents
import com.scichart.core.utility.touch.IReceiveMotionEvents
import com.scichart.core.utility.touch.ModifierTouchEventArgs

object TouchUtil {
    fun ModifierTouchEventArgs.overrideTouch(
        x: Float,
        y: Float,
        action: Int,
        recycle: Boolean = true
    ) = apply {
        if (recycle) e?.recycle()
        e = MotionEvent.obtain(
            SystemClock.uptimeMillis(),
            SystemClock.uptimeMillis(),
            action,
            x,
            y,
            0,
        )
    }

    fun ModifierTouchEventArgs.overrideTouch(
        target: IPublishMotionEvents,
        source: IReceiveMotionEvents,
        recycle: Boolean = true
    ) = run {
        ModifierTouchEventArgs(target, source)
            .overrideTouch(e.x, e.y, e.action, recycle)
    }
}