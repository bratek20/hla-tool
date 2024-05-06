plugins {
    //TODO should not publish artifact but fixtures needed
    alias(libs.plugins.bratek20.library.conventions)
}

dependencies {
    implementation(libs.assertj.core)

    implementation(libs.bratek20.architecture)
}