package com.banana.finchart.ui.screen.main

import android.content.res.AssetManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.banana.finchart.data.chart.ChartSnapshot
import com.banana.finchart.model.snapshot.SnapshotManager
import com.banana.finchart.ui.common.chart.modifier.SnapCrosshairModifier
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel @Inject constructor(
    private val snapshotManager: SnapshotManager
) : ViewModel() {

    val crosshairStateFlow = MutableStateFlow<SnapCrosshairModifier.State>(SnapCrosshairModifier.State.None)

    val activeChartSnapshot = MutableStateFlow<ChartSnapshot?>(null)

    fun loadChartsData(assetsManager: AssetManager, xlsFile: String, activateSymbol: String? = null) {
        viewModelScope.launch(Dispatchers.IO) {
            assetsManager.open(xlsFile).use { inputStream ->
                if (snapshotManager.loadChartFromXls(inputStream)) {
                    activateSymbol?.let { activateChart(it) }
                }

            }
        }
    }

    fun activateChart(symbol: String) {
        activeChartSnapshot.value = snapshotManager.getSnapshot(symbol)
    }
}