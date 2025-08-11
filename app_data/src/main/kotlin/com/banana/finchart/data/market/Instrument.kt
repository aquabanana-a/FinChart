package com.banana.finchart.data.market

data class Instrument(
    val symbol: String,
    val denominator: Int = 10000,
)