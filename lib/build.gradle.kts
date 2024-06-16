plugins {
    alias(libs.plugins.bratek20.library.conventions)
}

dependencies {
    // TODO use version catalog
    implementation("org.apache.velocity:velocity-engine-core:2.3")

    implementation(libs.bratek20.architecture)
    testImplementation(testFixtures(libs.bratek20.architecture))

    implementation(libs.bratek20.utils)
    testImplementation(testFixtures(libs.bratek20.utils))

    //TODO migrate fully to kotest
    testImplementation("org.junit.jupiter:junit-jupiter-params:${libs.versions.junit.get()}")

    // Kotest, TODO add to convention
    testImplementation("io.kotest:kotest-runner-junit5:5.9.1")
    testImplementation("io.kotest:kotest-assertions-core:5.9.1")
}

group = "pl.bratek20.hla"
version = "1.0.0-SNAPSHOT"