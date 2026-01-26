plugins {
    alias(libs.plugins.b20.simple.app)
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.bratek20.architecture)
    implementation(libs.bratek20.logs.logback)
}