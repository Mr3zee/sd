import io.ktor.plugin.features.*

@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("jvm")
    alias(libs.plugins.kotlin.plugin.serialization)
    alias(libs.plugins.ktor.plugin)
    application
    distribution
}

application {
    mainClass.set("StockKt")
}

dependencies {
    implementation(project(":server:base"))
}

ktor {
    docker {
        localImageName.set("sd-stock-service")
        imageTag.set("latest")
    }
}

tasks.register("prepareStock") {
    dependsOn("publishImageToLocalRegistry")
}
