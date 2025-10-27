plugins {
    alias(libs.plugins.bratek20.kotlin.library.conventions)
}

group = "com.github.bratek20.hla"
version = "1.0.20"

val velocityVersion = "2.3"

dependencies {
    implementation("org.apache.velocity:velocity-engine-core:$velocityVersion")

    implementation(libs.bratek20.architecture)
    testImplementation(testFixtures(libs.bratek20.architecture))

    implementation(libs.bratek20.logs.logback)
    testImplementation(testFixtures(libs.bratek20.logs.logback))

    api(libs.bratek20.utils)
    testFixturesImplementation(testFixtures(libs.bratek20.utils))
    testImplementation(testFixtures(libs.bratek20.utils))

    implementation(project(":code-builder"))
    implementation(kotlin("reflect"))
}