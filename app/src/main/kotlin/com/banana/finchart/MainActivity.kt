package com.banana.finchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import com.banana.finchart.ui.screen.main.MainScreen
import com.banana.finchart.ui.screen.main.MainScreenViewModel
import com.banana.finchart.ui.theme.FinChartTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var needShowKeyDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        val mainViewModel: MainScreenViewModel by viewModels()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val activeSnapshot by mainViewModel.activeChartSnapshot.collectAsState()

            FinChartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->

                    if (activeSnapshot == null) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f))
                                .padding(innerPadding)
                                .pointerInput(Unit) {
                                    awaitPointerEventScope {
                                        while (true) {
                                            awaitPointerEvent()
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    } else {
                        if (needShowKeyDialog) {
                            MainScreen.LicenseKeyDialog(onDismiss = { needShowKeyDialog = false })
                        }

                        MainScreen.TradingChart(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding),
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }
        }

        mainViewModel.loadChartsData(assets, "EURUSD_OHLCV.xlsx", "6E"/*"mock"*/)
    }
}