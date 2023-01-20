@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("js")
    alias(libs.plugins.kotlin.plugin.serialization)
}

kotlin {
    useJs()
}

dependencies {
    api(project(":common"))
    api(libs.kotlin.stdlib.js)
    api(enforcedPlatform(libs.kotlin.wrappers.bom))
    api(libs.react)
    api(libs.react.dom)
    api(libs.emotion)
    api(libs.ktor.client.js)
    api(libs.kotlinx.coroutines.core.js)
    api(libs.ktor.client.websockets)
}
