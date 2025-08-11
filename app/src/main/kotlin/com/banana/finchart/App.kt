package com.banana.finchart

import android.app.Application
import android.util.Log
import com.scichart.charting.visuals.SciChartSurface

class App : Application() {

    private val key =
        "CUgk0/hhLFRvVcqP55PaLQL0mdOlsL/WEKwyCtu4VlCI+egAm/RYDQuVDcriX6sO0S0+cVyOxIy8RRLLGwzPyBfx5z+RVcXx2ag9yBsZw07u2NHNcGILH4lSHGE5qhxC+UOO7bAzL4+G+w7MfEILvpCoBBdRV6/NsDiXMK7lFju29DurIvpL5IogAEnxb2vkvgWdzmdtZqkJOF26+yzBjuXV4ygIX8iygQ1Bu1fTq5RD9eizhOUn7T6zOav+8T4zPPpCzCV7hmdLMGWYqNzz8EZHakUsZYJCEe8agv6oGEho772h79t52IDfvwd2aB0yLW2SDhSdKOqmBsn/pcmX66ab2jKStJO1bRAJlRy1s0+IKoHhD+QhRAh7mHPWN5k6aWpycx+eEF4SC6fEMTWtrdKgy+rBlFADlJt5+TGFrAgLRSEb3ANCvdbeRg6X5iGM1bFogUy6t84bEV75DBFPxQv4K448HIF5De3yHuHDY1SFmRiCx6VRHM/JaWoX8Rk1oCAZzqqoAMmQfCefR1QhGF8LICu7YTlEyYGmTlMwrgydKOCL/QB3fO2al0f4fb+XwYK2AVI="

    override fun onCreate() {
        super.onCreate()

        try {
            SciChartSurface.setRuntimeLicenseKey(key);
        } catch (e: Exception) {
            Log.d("FinChart", "Invalid SciChart licence")
        }
    }
}