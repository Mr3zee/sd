@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
    application
    distribution
}

application {
    mainClass.set("ApplicationKt")
}

dependencies {
    implementation(project(":server:base"))
    testImplementation(libs.junit5.api)
    testRuntimeOnly(libs.junit5.engine)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit)
}

tasks.withType<Test> {
    dependsOn(":server:stock:prepareStock")
}
