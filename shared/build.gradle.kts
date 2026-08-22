import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
    alias(libs.plugins.room)
}

kotlin {
    // :app remains the installable Android application. This target publishes the
    // shared Kotlin/Compose code as an Android library consumed by that host.
    android {
        namespace = "com.example.recipe_app_compose.shared"
        compileSdk = 37
        minSdk = 28
        withHostTest {}
        androidResources.enable = true

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_18)
        }
    }

    val iosArm64 = iosArm64()
    val iosSimulatorArm64 = iosSimulatorArm64()

    // Both Apple targets export the same static framework API. The future Xcode
    // host selects the device or simulator binary without changing shared code.
    listOf(iosArm64, iosSimulatorArm64).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "RecipeComposeShared"
            isStatic = true
        }
    }

    sourceSets {
        // Only platform-neutral dependencies belong here. Android services and
        // Apple frameworks will be declared in their platform source sets later.
        commonMain.dependencies {
            implementation(libs.androidx.datastore.preferences)
            api(libs.androidx.room.runtime)
            implementation(libs.androidx.sqlite.bundled)
            implementation(libs.compose.animation)
            implementation(libs.compose.components.resources)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.runtime)
            implementation(libs.compose.ui)
            implementation(libs.coil3.compose)
            implementation(libs.coil3.network.ktor3)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.koin.compose)
            implementation(libs.koin.compose.viewmodel)
            implementation(libs.koin.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.kotlinx.serialization.json)
        }

        androidMain.dependencies {
            // Android owns its system back dispatcher; common onboarding only
            // depends on the small expect/actual adapter around this API.
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.okhttp)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        // These tests are compiled for every enabled target. Android host tests
        // are explicitly enabled above because the Android-KMP plugin defaults off.
        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.koin.test)
            implementation(libs.ktor.client.mock)
        }
    }
}

dependencies {
    add("kspAndroid", libs.androidx.room.compiler)
    add("kspIosArm64", libs.androidx.room.compiler)
    add("kspIosSimulatorArm64", libs.androidx.room.compiler)
}

room {
    schemaDirectory("$projectDir/schemas")
}

compose.resources {
    // A stable package keeps generated Res imports predictable as files move.
    packageOfResClass = "com.example.recipe_app_compose.shared.generated.resources"
}
