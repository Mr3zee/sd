@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(project(":common"))
    implementation(libs.ktor.client.core)
    implementation(libs.logback.classic)
    implementation(libs.ktor.client.cio)
    implementation(libs.kotlinx.coroutines.core.jvm)
    implementation(libs.kotlinx.cli)
}
