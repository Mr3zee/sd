import org.jetbrains.kotlin.gradle.dsl.KotlinJsProjectExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsBrowserDsl
import org.jetbrains.kotlin.gradle.targets.js.dsl.KotlinJsTargetDsl
import org.jetbrains.kotlin.gradle.targets.js.webpack.KotlinWebpackConfig


fun KotlinMultiplatformExtension.useJs() {
    js(IR) {
        useJsInternal()
    }
}

fun KotlinJsProjectExtension.useJs(browserConfig: KotlinJsBrowserDsl.() -> Unit = {}) {
    js(IR) {
        useJsInternal(browserConfig)
    }
}

private fun KotlinJsTargetDsl.useJsInternal(browserConfig: KotlinJsBrowserDsl.() -> Unit = {}) {
    browser {
        binaries.executable()
        browserConfig()
    }
}

fun KotlinJsBrowserDsl.withWebpack(port: Int, proxy: String, addintionalConfig: KotlinWebpackConfig.() -> Unit = {}) {
    commonWebpackConfig {
        cssSupport {
            enabled = true
        }

        val proxies = devServer?.proxy ?: mutableMapOf()
        listOf(
            "/api",
            "/images",
        ).forEach {
            proxies[it] = proxy
        }

        devServer = devServer?.copy(
            port = port,
            proxy = proxies
        )

        addintionalConfig()
    }
}
