plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
    alias(libs.plugins.bratek20.kotest.conventions)
}

dependencies {
    implementation(libs.bratek20.architecture)
    testImplementation(testFixtures(libs.bratek20.architecture))

    //TODO why it does not work from kotest conventions?
    testImplementation(libs.kotest.runner.junit5)
}