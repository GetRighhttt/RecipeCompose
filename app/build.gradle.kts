import java.util.Properties

plugins {
    // Existing plugins
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("com.google.devtools.ksp") // ksp
    id("com.google.gms.google-services") // google-services
}

android {
    namespace = "com.example.recipe_app_compose"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.recipe_app_compose"
        minSdk = 28
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val properties = Properties()
        properties.load(project.rootProject.file("local.properties").inputStream())

        buildConfigField("String", "YELP_API_KEY", "\"${properties.getProperty("YELP_API_KEY")}\"")
        buildConfigField("String", "YELP_BASE_URL", "\"${properties.getProperty("YELP_BASE_URL")}\"")
        buildConfigField("String", "BASE_URL", "\"${properties.getProperty("BASE_URL")}\"")
    }

    buildFeatures {
        buildConfig = true
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
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
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.maps.compose)

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
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}