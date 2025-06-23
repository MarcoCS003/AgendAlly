// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    id("com.google.devtools.ksp") version "2.1.21-2.0.2" apply false
    id("org.jetbrains.kotlin.plugin.serialization") version "2.1.21"
    id("com.google.dagger.hilt.android") version "2.56.2" apply false
}
tasks.register("signingReport", Exec::class) {
    group = "android"
    description = "Prints the SHA-1 and SHA-256 keys"
    commandLine("keytool", "-list", "-v",
        "-alias", "androiddebugkey",
        "-keystore", "${System.getProperty("user.home")}/.android/debug.keystore",
        "-storepass", "android",
        "-keypass", "android")
}