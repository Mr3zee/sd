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
    testImplementation(libs.junit5)
    testImplementation(libs.testcontainers)
    testImplementation(libs.testcontainers.junit)
    testImplementation(libs.testcontainers.postgresql)
    testImplementation(libs.koin.test.junit5)
}

tasks.withType<Test> {
    dependsOn(":server:stock:prepareStock")
}

tasks.test {
    useJUnitPlatform()
}
