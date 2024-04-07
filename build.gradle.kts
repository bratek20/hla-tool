plugins {
    alias(libs.plugins.bratek20.library.conventions)
}

dependencies {
    implementation("org.apache.velocity:velocity-engine-core:2.3") // TODO use version catalog
}

group = "pl.bratek20.hla"
version = "1.0.0-SNAPSHOT"