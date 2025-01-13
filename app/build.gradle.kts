import org.gradle.kotlin.dsl.support.kotlinCompilerOptions

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
//    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0"
}

android {
    namespace = "bg.zahov.fitness.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "bg.zahov.app"
        minSdk = 31
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
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    buildFeatures {
        viewBinding = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.3"
    }
}

dependencies {
    implementation("androidx.compose.ui:ui-test-junit4-android:1.7.5")
    implementation("junit:junit:4.12")
    implementation("androidx.compose.foundation:foundation-layout-android:1.7.3")
    val composeBom = platform("androidx.compose:compose-bom:2024.09.03")
    implementation(composeBom)
    androidTestImplementation(composeBom)
    androidTestImplementation("androidx.compose.ui:ui-test-junit4:1.7.3")
    debugImplementation("androidx.compose.ui:ui-test-manifest:1.7.3")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    implementation("com.google.accompanist:accompanist-systemuicontroller:0.30.1")
    androidTestImplementation("androidx.test:runner:1.5.2")
    implementation("androidx.navigation:navigation-compose:2.8.5")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-tooling-preview")
    debugImplementation("androidx.compose.ui:ui-tooling")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.activity:activity-compose:1.9.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.6")
    implementation("com.google.firebase:firebase-functions-ktx:21.0.0")
    implementation("com.google.firebase:firebase-auth-ktx:23.1.0")
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.navigation:navigation-fragment-ktx:2.8.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6")
    implementation("com.google.firebase:firebase-database-ktx:21.0.0")
    implementation("com.google.firebase:firebase-firestore-ktx:25.1.1")
    testImplementation("junit:junit:4.13.2")
    implementation("com.mikhaellopez:circularprogressbar:3.1.0")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6")
    implementation("com.google.android.flexbox:flexbox:3.0.0")
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")
    implementation("com.kizitonwose.calendar:compose:2.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}