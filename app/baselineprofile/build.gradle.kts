plugins {
    alias(libs.plugins.android.test)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.ksp)
}

android {
    namespace = "com.example.baselineprofile"
    compileSdk = 35

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        buildConfig = true
    }

    kotlinOptions {
        jvmTarget = "11"
    }

    defaultConfig {
        minSdk = 28
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["androidx.benchmark.suppressErrors"] = "LOW-BATTERY"
    }

    targetProjectPath = ":app"
}

dependencies {
    implementation(libs.androidxTestExtJunit)
    implementation(libs.espressoCore)
    implementation(libs.androidx.uiautomator)
    implementation(libs.androidx.benchmark.macro.junit4)
}

androidComponents {
    onVariants { v ->
        val artifactsLoader = v.artifacts.getBuiltArtifactsLoader()
        v.instrumentationRunnerArguments.put(
            "targetAppId",
            v.testedApks.map { artifactsLoader.load(it)?.applicationId }
        )
    }
}