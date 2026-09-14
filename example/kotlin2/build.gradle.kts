plugins {
    alias(libs.plugins.b20.library)
}

dependencies {
    implementation(libs.bratek20.architecture)

    implementation(project(":kotlin"))
    testFixturesImplementation(testFixtures(project(":kotlin")))
}
