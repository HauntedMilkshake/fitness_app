plugins {
    alias(libs.plugins.googleServices)
    alias(libs.plugins.kotlinSerialization)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.baselineprofile)
    alias(libs.plugins.screenshot)
    alias(libs.plugins.roborazzi)
    alias(libs.plugins.paparazzi)
}

android {
    namespace = "bg.zahov.fitness.app"
    compileSdk = 35
    flavorDimensions += "default"
    experimentalProperties["android.experimental.enableScreenshotTest"] = true

    buildFeatures {
        compose = true
    }

    productFlavors {
        create("mock") {
            dimension = "default"
            applicationId = "bg.zahov.fitness.app"
            applicationIdSuffix = ".mock"
            versionName = "-mock"
        }
        create("production") {
            dimension = "default"
            applicationId = "bg.zahov.fitness.app"
            applicationIdSuffix = ".production"
            versionName = "-production"
        }
    }

    sourceSets {
        getByName("production") {
            java.srcDirs("src/production")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    defaultConfig {
        applicationId = "bg.zahov.fitness.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
        packagingOptions.resources.excludes.add("META-INF/versions/9/OSGI-INF/MANIFEST.MF")
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

dependencies {
    implementation(libs.androidx.profileinstaller)
    "baselineProfile"(project(":app:baselineprofile"))
    val composeBom = platform(libs.composeBom)
    implementation(composeBom)
    androidTestImplementation(composeBom)
    implementation(libs.androidx.ktx)
    implementation(libs.splashscreen)
    implementation(libs.composeUiTestJunit4)
    implementation(libs.junit)
    implementation(libs.kotlinSerialization)
    androidTestImplementation(libs.composeUiTestJunit4)
    androidTestImplementation(libs.androidxTestExtJunit)
    androidTestImplementation(libs.androidxTestRunner)
    implementation(libs.navigationCompose)
    implementation(libs.material)
    implementation(libs.ui)
    debugImplementation(libs.uiTooling)
    implementation(libs.uiToolingPreview)
    implementation(libs.material3)
    implementation(libs.activityCompose)
    implementation(libs.lifecycleViewModelCompose)
    implementation(libs.firebaseFunctions)
    implementation(libs.firebaseAuth)
    implementation(libs.androidx.ktx)
    implementation(libs.appcompat)
    implementation(libs.google.services)
    implementation(libs.lifeCycleRuntime)
    implementation(libs.firebaseFirestore)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espressoCore)
    implementation(libs.coroutines)
    implementation(libs.lifecycleLiveData)
    implementation(libs.mpChart)
    implementation(libs.calendarCompose)
    implementation(libs.numberPicker)
    implementation(libs.hiltAndroid)
    implementation(libs.hiltNavigation)
    ksp(libs.hiltCompiler)
    implementation(libs.roboelectric)
    screenshotTestImplementation(libs.uiTooling)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazziCompose)
    testImplementation(libs.paparazzi)
}