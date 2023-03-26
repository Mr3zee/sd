@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
}

tasks.test {
    useJUnitPlatform()
}

dependencies {
    api(project(":common"))
    api(libs.ktor.client.cio)
    api(libs.ktor.server.cio)
    api(libs.ktor.server.core.jvm)
    api(libs.ktor.server.host.common.jvm)
    api(libs.ktor.server.status.pages.jvm)
    api(libs.ktor.server.content.negotiation)
    api(libs.ktor.server.cors.jvm)
    api(libs.ktor.server.call.logging.jvm)
    api(libs.ktor.serialization.kotlinx.json)
    api(libs.kotlinx.coroutines.core.jvm)
    api(libs.logback.classic)
    api(libs.exposed.core)
    api(libs.exposed.dao)
    api(libs.exposed.jdbc)
    api(libs.postgresql)
    api(libs.koin.ktor)
    api(libs.koin.logger.slf4j)
}
