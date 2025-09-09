// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    // Existing plugins
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    id("androidx.room") version "2.7.2" apply false
    id("com.google.devtools.ksp") version "2.2.20-RC2-2.0.2" apply false
    // Add the dependency for the Google services Gradle plugin
    id("com.google.gms.google-services") version "4.4.3" apply false
    // Add the dependency for the Performance Monitoring Gradle plugin
    id("com.google.firebase.firebase-perf") version "2.0.1" apply false
}