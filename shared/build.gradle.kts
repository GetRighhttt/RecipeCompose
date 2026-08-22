import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

val localProperties = Properties().apply {
    val propertiesFile = rootProject.file("local.properties")
    if (propertiesFile.isFile) {
        propertiesFile.inputStream().use(::load)
    }
}
val generatedIosConfigurationDirectory =
    layout.buildDirectory.dir("generated/iosYelpConfiguration/iosMain")
val generateIosYelpConfiguration = tasks.register("generateIosYelpConfiguration") {
    val escapedApiKey = localProperties.getProperty("YELP_API_KEY", "")
        .replace("\\", "\\\\")
        .replace("\"", "\\\"")
        .replace("$", "\\$")
    inputs.property("yelpApiKey", escapedApiKey)
    outputs.dir(generatedIosConfigurationDirectory)
    doLast {
        val output = generatedIosConfigurationDirectory.get().file(
            "com/example/recipe_app_compose/features/location/data/remote/LocalIosYelpConfiguration.kt"
        ).asFile
        output.parentFile.mkdirs()
        output.writeText(
            """
            package com.example.recipe_app_compose.features.location.data.remote

            /** Generated locally from the ignored root local.properties file. */
            internal const val localIosYelpApiKey: String = "$escapedApiKey"
            """.trimIndent()
        )
    }
}

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
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.lifecycle.runtime.compose)
            implementation(libs.ktor.client.okhttp)
            implementation(libs.maps.compose)
            implementation(libs.play.services.location)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        getByName("iosMain").kotlin.srcDir(generatedIosConfigurationDirectory)

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

tasks.matching {
    it.name.startsWith("compileKotlinIos") || it.name.startsWith("kspKotlinIos")
}.configureEach {
    dependsOn(generateIosYelpConfiguration)
}

// KSP contributes generated Room sources to Android host tests. Gradle 9.7
// requires lint's model writer to declare that producer explicitly.
tasks.matching {
    it.name == "generateAndroidHostTestLintModel" ||
        it.name == "lintAnalyzeAndroidHostTest"
}.configureEach {
    dependsOn("kspAndroidHostTest")
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
