plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
}

dependencies {
    implementation(libs.bratek20.architecture)
    testImplementation(testFixtures(libs.bratek20.architecture))
}