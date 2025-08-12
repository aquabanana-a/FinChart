package com.banana.finchart.model.snapshot

import com.banana.finchart.data.chart.ChartSnapshot
import com.banana.finchart.data.market.Instrument
import com.banana.finchart.data.market.OHLC
import org.apache.poi.ss.usermodel.DataFormatter
import org.apache.poi.ss.usermodel.DateUtil
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

class SnapshotManagerImpl @Inject constructor() : SnapshotManager {

    private val snapshotMap = hashMapOf(
        "mock" to ChartSnapshot( // mock for test
            Instrument("mock"), setOf(
                OHLC(1622505600000L, 10.0, 12.0, 9.5, 11.5, 1000),
                OHLC(1622592000000L, 11.0, 12.5, 10.5, 11.0, 1500),
                OHLC(1622678400000L, 10.5, 11.5, 9.8, 11.2, 1300),
                OHLC(1622764800000L, 12.0, 13.0, 11.0, 12.5, 1100),
                OHLC(1622851200000L, 11.5, 12.0, 10.8, 11.0, 1600),
                OHLC(1622937600000L, 11.2, 11.8, 10.9, 11.5, 1400),
                OHLC(1623024000000L, 11.4, 11.9, 11.0, 11.2, 1350),
                OHLC(1623110400000L, 11.1, 12.3, 10.7, 12.0, 1700),
                OHLC(1623196800000L, 11.8, 12.5, 11.4, 12.3, 1550),
                OHLC(1623283200000L, 12.2, 12.7, 11.8, 12.1, 1800),
                OHLC(1623369600000L, 12.0, 12.4, 11.5, 12.2, 1650),
                OHLC(1623456000000L, 12.5, 12.8, 12.0, 12.6, 1900),
                OHLC(1623542400000L, 12.3, 12.6, 12.0, 12.1, 1750),
                OHLC(1623628800000L, 12.0, 12.3, 11.6, 11.9, 1600),
                OHLC(1623715200000L, 11.7, 12.1, 11.4, 11.6, 1500),
                OHLC(1623801600000L, 11.5, 12.4, 11.3, 11.7, 1550),
                OHLC(1623888000000L, 11.8, 12.7, 11.5, 12.0, 1600),
                OHLC(1623974400000L, 12.1, 12.9, 11.8, 12.3, 1700),
                OHLC(1624060800000L, 11.9, 12.5, 11.6, 12.0, 1650),
                OHLC(1624147200000L, 12.3, 12.8, 11.9, 12.5, 1750),
                OHLC(1624233600000L, 12.5, 13.0, 12.0, 12.7, 1800),
                OHLC(1624320000000L, 12.7, 13.2, 12.2, 12.8, 1850),
                OHLC(1624406400000L, 12.4, 13.1, 12.0, 12.5, 1900),
                OHLC(1624492800000L, 12.6, 13.3, 12.1, 12.7, 1950),
                OHLC(1624579200000L, 12.8, 13.4, 12.3, 12.9, 2000)
            )
        )
    )

    override fun getSnapshot(symbol: String): ChartSnapshot? {
        return snapshotMap[symbol]
    }

    override fun loadChartFromXls(stream: InputStream): Boolean {
        try {
            val xlsData = hashMapOf<String, HashMap<Long, OHLC>>()

            val workbook = XSSFWorkbook(stream)
            val sheet = workbook.getSheetAt(0)
            val dataFormatter = DataFormatter()
            val dateFormat = SimpleDateFormat("dd.MM.yyyy H:mm", Locale.getDefault())

            for (rowIndex in 1 until sheet.physicalNumberOfRows) {
                val row = sheet.getRow(rowIndex) ?: continue

                val symbol = row.getCell(0).stringCellValue ?: continue

                val tsCell = row.getCell(1)
                var timestampMs = 0L
                if (DateUtil.isCellDateFormatted(tsCell)) {
                    val date = tsCell.dateCellValue
                    timestampMs = date.time
                } else {
                    val tsStr = dataFormatter.formatCellValue(tsCell).replace(',', '.')
                    val date = dateFormat.parse(tsStr) ?: continue
                    timestampMs = date.time
                }

                val openStr = dataFormatter.formatCellValue(row.getCell(2)).replace(',', '.')
                val open = openStr.toDoubleOrNull() ?: continue

                val highStr = dataFormatter.formatCellValue(row.getCell(3)).replace(',', '.')
                val high = highStr.toDoubleOrNull() ?: continue

                val lowStr = dataFormatter.formatCellValue(row.getCell(4)).replace(',', '.')
                val low = lowStr.toDoubleOrNull() ?: continue

                val closeStr = dataFormatter.formatCellValue(row.getCell(5)).replace(',', '.')
                val close = closeStr.toDoubleOrNull() ?: continue

                val volumeStr = dataFormatter.formatCellValue(row.getCell(6)).replace(',', '.')
                val volume = volumeStr.toLongOrNull() ?: 0L

                var symbolData = xlsData[symbol]
                if (symbolData == null) {
                    symbolData = hashMapOf()
                    xlsData[symbol] = symbolData
                }

                symbolData[timestampMs] = OHLC(
                    timestampMs = timestampMs,
                    openPrice = open,
                    highPrice = high,
                    lowPrice = low,
                    closePrice = close,
                    volume = volume
                )
            }

            workbook.close()

            xlsData.forEach { (symbol, data) ->
                val knownSnapshot = snapshotMap[symbol]
                snapshotMap[symbol] = ChartSnapshot(
                    Instrument(symbol),
                    (if (knownSnapshot != null)
                        data.values + knownSnapshot.ohlcDataSet
                    else
                        data.values).sortedBy { it.timestampMs }.toSet()
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            return false
        }
        return true
    }
}