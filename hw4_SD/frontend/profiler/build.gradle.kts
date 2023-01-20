@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("js")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(project(":frontend:base"))
}

kotlin {
    useJs {
        withWebpack(
            port = 3001,
            proxy = "http://localhost:8083",
        )
    }
}
