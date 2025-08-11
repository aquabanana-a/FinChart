package com.banana.finchart.ui.common.util

import kotlin.math.pow

object FormatUtil {

    fun Float.formatWithComma(digits: Int) = this.toDouble().formatWithComma(digits)

    fun Double.formatWithComma(digits: Int): String {
        val factor = 10.0.pow(digits)
        val truncated = kotlin.math.floor(this * factor) / factor

        val formatted = "%.${digits}f".format(truncated)
        return formatted.replace('.', ',')
    }

    fun Float.format(digits: Int) = this.toDouble().format(digits)

    fun Double.format(digits: Int) = "%.${digits}f".format(this)

    fun Int.format(digits: Int) = this.toLong().format(digits)

    fun Long.format(digits: Int) = "%.${digits}f".format(this)
}