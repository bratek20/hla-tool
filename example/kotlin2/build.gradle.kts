plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
}

dependencies {
    implementation(libs.assertj.core)

    implementation(libs.bratek20.architecture)

    implementation(project(":kotlin"))
    testFixturesImplementation(testFixtures(project(":kotlin")))
}