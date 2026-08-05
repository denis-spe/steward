plugins {
    alias(libs.plugins.android.test)
}

android {
    namespace = "com.den.steward.benchmark"
    compileSdk = 37

    defaultConfig {
        minSdk = 26
        targetSdk = 37

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "DEBUGGABLE,NOT-SELF-INSTRUMENTING"
    }

    targetProjectPath = ":app"
    experimentalProperties["android.experimental.self-instrumenting"] = true

    buildTypes {
        debug {
            isMinifyEnabled = false
        }
        create("benchmark") {
            initWith(getByName("debug"))
            signingConfig = signingConfigs.getByName("debug")
            matchingFallbacks.add("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.junit)
    implementation(libs.androidx.espresso.core)
    implementation(libs.androidx.benchmark.macro.junit4)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.runner)
}
