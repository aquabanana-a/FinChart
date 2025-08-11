package com.banana.finchart.data.chart

import com.banana.finchart.data.market.Instrument
import com.banana.finchart.data.market.OHLC

class ChartSnapshot(
    val instrument: Instrument,
    dataList: List<OHLC>? = null
) {
    private val _ohlcDataList = ArrayList<OHLC>()

    val ohlcDataList: List<OHLC>
        get() = _ohlcDataList

    init {
        dataList?.let { _ohlcDataList.addAll(it) }
    }

    fun push(ohlc: OHLC) {
        _ohlcDataList.add(ohlc)
    }
}