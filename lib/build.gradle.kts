plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
}

val velocityVersion = "2.3"

dependencies {
    implementation("org.apache.velocity:velocity-engine-core:$velocityVersion")

    implementation(libs.bratek20.architecture)
    testImplementation(testFixtures(libs.bratek20.architecture))

    implementation(libs.bratek20.logs.logback)
    testImplementation(testFixtures(libs.bratek20.logs.logback))

    api(project(":external"))
    testFixturesImplementation(testFixtures(project(":external")))
    testImplementation(testFixtures(project(":external")))

    implementation(project(":code-builder"))
}