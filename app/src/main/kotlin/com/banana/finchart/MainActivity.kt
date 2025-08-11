package com.banana.finchart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.banana.finchart.ui.screen.main.MainScreen
import com.banana.finchart.ui.screen.main.MainScreenViewModel
import com.banana.finchart.ui.theme.FinChartTheme

class MainActivity : ComponentActivity() {

    private var needShowKeyDialog by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            FinChartTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val mainViewModel: MainScreenViewModel = viewModel()

                    val needShowDialog = needShowKeyDialog
                    if (needShowDialog) {
                        MainScreen.LicenseKeyDialog(onDismiss = { needShowKeyDialog = false })
                    } else {
                        MainScreen.TradingChart(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            mainViewModel = mainViewModel
                        )
                    }
                }
            }
        }
    }
}