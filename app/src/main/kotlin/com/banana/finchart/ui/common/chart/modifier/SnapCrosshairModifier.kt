package com.banana.finchart.ui.common.chart.modifier

import android.view.MotionEvent
import android.view.ViewConfiguration
import com.banana.finchart.ui.common.util.TouchUtil.overrideTouch
import com.scichart.charting.model.dataSeries.OhlcDataSeries
import com.scichart.charting.modifiers.CursorModifier
import com.scichart.core.IServiceContainer
import com.scichart.core.utility.touch.ModifierTouchEventArgs
import com.scichart.data.model.ISciList
import com.scichart.data.numerics.SearchMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.util.Date

class SnapCrosshairModifier(
    private val candleDataSeries: OhlcDataSeries<Date, Double>,
    private val stateFlow: MutableStateFlow<State>,
) : CursorModifier() {

    sealed class State(
        val timestampMs: Long
    ) {
        object None : State(-1)
        class Active(timestampMs: Long) : State(timestampMs)
    }

    // Handler + Looper is so trivial
    private var uiScope: CoroutineScope? = null
    private var longPressJob: Job? = null
    private var longPressToggle = false
    private val longPressTimeoutMs = ViewConfiguration.getLongPressTimeout().toLong()

    private var modifierTouchHandler: ((ModifierTouchEventArgs) -> Unit)? = null

    private var pressToggle = false

    var pX = 0f
    var pY = 0f

    override fun onTouch(touchEventArgs: ModifierTouchEventArgs) {
        val xCalc = xAxis.currentCoordinateCalculator ?: return

        when (touchEventArgs.e.action) {
            MotionEvent.ACTION_DOWN -> {
                if (longPressToggle) return

                val candleTimestampMs = findNearestCandleTimestampMs(touchEventArgs.e.x)
                if (candleTimestampMs < 0) return

                val candleX = xCalc.getCoordinate(candleTimestampMs.toDouble())

                pX = candleX
                pY = touchEventArgs.e.y

                longPressJob?.cancel()
                longPressJob = uiScope?.launch {
                    delay(longPressTimeoutMs)

                    longPressToggle = true

                    stateFlow.value = State.Active(candleTimestampMs)

                    proceedModifierTouch(
                        touchEventArgs.overrideTouch(
                            pX,
                            pY,
                            MotionEvent.ACTION_DOWN
                        ))
                }
            }
            MotionEvent.ACTION_MOVE -> {
                longPressJob?.cancel()
                longPressJob = null

                if (!longPressToggle) return

                val candleTimestampMs = findNearestCandleTimestampMs(touchEventArgs.e.x)
                if (candleTimestampMs < 0) return

                val candleX = xCalc.getCoordinate(candleTimestampMs.toDouble())

                pX = candleX
                pY = touchEventArgs.e.y

                stateFlow.value = State.Active(candleTimestampMs)

                proceedModifierTouch(
                    touchEventArgs.overrideTouch(
                        pX,
                        pY,
                        MotionEvent.ACTION_MOVE
                    ))
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                longPressJob?.cancel()
                longPressJob = null

                longPressToggle = false

                stateFlow.value = State.None

                proceedModifierTouch(touchEventArgs)
            }
        }
    }

    private fun proceedModifierTouch(touchEventArgs: ModifierTouchEventArgs) {
        super.onTouch(touchEventArgs)
        modifierTouchHandler?.invoke(touchEventArgs)
    }

    private fun findNearestCandleTimestampMs(touchX: Float): Long {
        val surface = parentSurface ?: return -1

        val xAxis = surface.xAxes.firstOrNull() ?: return -1
        val xCalc = xAxis.currentCoordinateCalculator ?: return -1

        val touchXData = xCalc.getDataValue(touchX)
        val xList = candleDataSeries.xValues as ISciList<Date>
        val index = xList.findIndex(Date(touchXData.toLong()), SearchMode.Nearest, true)

        if (index < 0) return -1

        return xList[index].time
    }

    override fun attachTo(services: IServiceContainer?) {
        uiScope = CoroutineScope(SupervisorJob() + Dispatchers.Main.immediate)
        super.attachTo(services)
    }

    override fun detach() {
        uiScope?.cancel()
        uiScope = null
        super.detach()
    }

    fun setModifierTouchListener(handler: ((ModifierTouchEventArgs) -> Unit)?) {
        this.modifierTouchHandler = handler
    }
}