plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    kotlin("kapt")
    alias(libs.plugins.hilt.android)
}
hilt {
    enableAggregatingTask = false
}
android {
    namespace = "com.banana.finchart"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.banana.finchart"
        minSdk = 28
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(project(":app_data"))
    implementation(project(":app_model"))

    implementation(libs.bundles.kotlin)
    implementation(libs.bundles.coroutines)
    implementation(libs.bundles.ui)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.coil.compose)
    implementation(libs.lifecycle.viewmodel.compose)

    implementation(libs.bundles.scichart2d)
//    scichart2d()

    implementation(libs.hilt.android)
    kapt(libs.hilt.android.compiler)

    implementation(libs.androidx.security.crypto)
}

fun DependencyHandler.scichart2d() {
    implementation(libs.scichart.core) { artifact { type = "aar" } }
    implementation(libs.scichart.data) { artifact { type = "aar" } }
    implementation(libs.scichart.drawing) { artifact { type = "aar" } }
    implementation(libs.scichart.charting) { artifact { type = "aar" } }
    implementation(libs.scichart.extensions) { artifact { type = "aar" } }
}

fun DependencyHandler.scichart3d() {
    implementation(libs.scichart.charting3d) { artifact { type = "aar" } }
    implementation(libs.scichart.extensions3d) { artifact { type = "aar" } }
}