package com.banana.finchart.data

import java.util.Date

data class OHLC(
    val timestampMs: Long,
    val openPrice: Double,
    val highPrice: Double,
    val lowPrice: Double,
    val closePrice: Double,
    val volume: Long,
) {
    val timestampDate get() = Date(timestampMs)
}