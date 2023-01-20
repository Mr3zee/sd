@Suppress("DSL_SCOPE_VIOLATION") // "libs" produces a false-positive warning, see https://youtrack.jetbrains.com/issue/KTIJ-19369
plugins {
    kotlin("js")
    alias(libs.plugins.kotlin.plugin.serialization)
}

dependencies {
    implementation(project(":frontend:base"))
    implementation(libs.mui)
    implementation(libs.mui.icons)
}

kotlin {
    useJs {
        withWebpack(
            port = 3000,
            proxy = "http://localhost:8080"
        )
    }
}
