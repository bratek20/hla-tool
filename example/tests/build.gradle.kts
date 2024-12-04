plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
}

dependencies {
    implementation(libs.assertj.core)

    implementation(libs.bratek20.architecture)

    implementation(project(":kotlin"))
    testImplementation(testFixtures(project(":kotlin")))

    implementation(project(":kotlin2"))
    testImplementation(testFixtures(project(":kotlin2")))

    testImplementation(testFixtures(libs.bratek20.architecture))
    testImplementation(testFixtures(libs.bratek20.infrastructure))
}