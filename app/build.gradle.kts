plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlinx-serialization")
    id("com.google.devtools.ksp") // ✅ CAMBIAR: Usar KSP en lugar de KAPT
    id("com.google.dagger.hilt.android")
}

android {
    namespace = "com.example.academically"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.academically"
        minSdk = 24
        //noinspection OldTargetApi
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}


dependencies {
    // --- AndroidX Core y Utilidades ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlinx.datetime)

    // --- UI (Jetpack Compose) ---
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended)
    implementation(libs.androidx.activity.compose)

    // Herramientas de UI adaptativa
    implementation("androidx.compose.material3:material3-window-size-class:1.3.2")
    implementation(libs.androidx.window)
    implementation(libs.androidx.material3.adaptive.navigation.suite)
    implementation(libs.accompanist.adaptive)

    // --- Navegación Compose ---
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // --- ViewModel y Ciclo de Vida ---
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // --- Base de Datos (Room) ---
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler) // ✅ CAMBIAR: KSP en lugar de kapt

    // --- Hilt (Inyección de Dependencias) ---
    implementation(libs.hilt.android) // ✅ ACTUALIZAR: Última versión
    ksp("com.google.dagger:hilt-compiler:2.56.2") // ✅ CAMBIAR: KSP en lugar de kapt

    // --- Seguridad (EncryptedSharedPreferences) ---
    implementation(libs.androidx.security.crypto)

    // --- Autenticación y OAuth ---
    implementation(libs.androidx.credentials)
    implementation(libs.play.services.auth)
    implementation(libs.googleid)

    // --- Corrutinas ---
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // --- HTTP Networking (Ktor) ---
    implementation(libs.ktor.client.android)
    implementation(libs.ktor.client.content.negotiation)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.logging)

    // --- HTTP Networking (Retrofit + Gson) ---
    implementation(libs.retrofit)
    implementation(libs.converter.gson)
    implementation(libs.logging.interceptor)

    // --- Imágenes ---
    implementation(libs.coil.compose)

    // --- Google Maps / Places ---
    implementation(libs.places)

    // --- Material (Legacy Material Design) ---
    implementation(libs.material)

    // --- Testing ---
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)

    // --- Debug ---
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}