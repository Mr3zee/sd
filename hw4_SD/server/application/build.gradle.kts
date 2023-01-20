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
}

val buildAndCopyFrontend = tasks.register<Copy>("buildAndCopyFrontend") {
    val frontendDist = project(":frontend:application").tasks.named("browserDistribution")
    dependsOn(frontendDist)
    from(frontendDist)
    into("${project.projectDir}/src/main/resources/static")
}

val prepareAppResources = tasks.register("prepareAppResources") {
    dependsOn(buildAndCopyFrontend)
    finalizedBy("processResources")
}

val buildApp = tasks.register("buildApp") {
    dependsOn(prepareAppResources)
    finalizedBy("build")
}

tasks.named<JavaExec>("run") {
    dependsOn(buildApp)
    classpath(tasks.named<Jar>("jar"))
}
