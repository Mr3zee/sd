allprojects {
    tasks.withType<Test>() {
        useJUnitPlatform()
    }
}