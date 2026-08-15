plugins {
    // Existing plugins
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.secrets.gradle)
    id("com.google.devtools.ksp") // ksp
    id("com.google.gms.google-services") // google-services
}

android {
    namespace = "com.example.recipe_app_compose"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.example.recipe_app_compose"
        minSdk = 28
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

    }

    buildFeatures {
        buildConfig = true
        compose = true
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
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

kotlin {
    compilerOptions {
        languageVersion = org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_4
        freeCompilerArgs.add("-Xexplicit-backing-fields")
    }
}

secrets {
    propertiesFileName = "local.properties"
    defaultPropertiesFileName = "local.defaults.properties"
    ignoreList.add("sdk.dir")
}

dependencies {
    // viewmodel
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose.v286)

    // LiveData
    implementation(libs.androidx.lifecycle.livedata.ktx)

    // Jetpack Compose navigation
    implementation(libs.androidx.navigation.compose)

    // ROOM Database
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)

    // Google - Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics) // analytics
    implementation(libs.firebase.perf) // performance
    implementation(libs.firebase.firestore) // firestore
    implementation(libs.firebase.auth) // authentication

    // Google play - Maps
    implementation(libs.maps.compose)
    implementation(libs.play.services.location)

    // network
    implementation(libs.retrofit)

    // logging
    implementation(libs.okhttp)
    implementation(libs.logging.interceptor)

    // Gson
    implementation(libs.converter.gson)

    // image loading with coil
    implementation(libs.coil.compose)

    // glide just in case
    implementation(libs.glide)
    annotationProcessor(libs.compiler)

    // splash screen
    implementation(libs.androidx.core.splashscreen)

    // material 3
    implementation(libs.androidx.material3)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)

    // material icons
    implementation(libs.androidx.material.icons.core)
    implementation(libs.androidx.material.icons.extended)

    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(platform(libs.androidx.compose.bom))
}
