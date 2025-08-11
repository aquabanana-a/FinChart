package com.banana.finchart.ui.screen.main

import androidx.lifecycle.ViewModel
import com.banana.finchart.ui.common.chart.SnapCrosshairModifier
import kotlinx.coroutines.flow.MutableStateFlow

class MainScreenViewModel: ViewModel() {
    val crosshairStateFlow = MutableStateFlow<SnapCrosshairModifier.State>(SnapCrosshairModifier.State.None)
}