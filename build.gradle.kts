buildscript {
    dependencies {
        classpath(libs.google.services)
        classpath(libs.gradle)
    }
}
plugins {
    alias(libs.plugins.googleServices) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinSerialization) apply false
    alias(libs.plugins.hilt) apply false
    alias(libs.plugins.ksp) apply false
}