package com.banana.finchart.model.snapshot

import com.banana.finchart.data.chart.ChartSnapshot
import java.io.InputStream

interface SnapshotManager {
    fun getSnapshot(symbol: String): ChartSnapshot?
    fun loadChartFromXls(stream: InputStream): Boolean
}