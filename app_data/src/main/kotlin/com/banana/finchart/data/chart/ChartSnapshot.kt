package com.banana.finchart.data.chart

import com.banana.finchart.data.market.Instrument
import com.banana.finchart.data.market.OHLC

class ChartSnapshot(
    val instrument: Instrument,
    val ohlcDataSet: Set<OHLC>
)