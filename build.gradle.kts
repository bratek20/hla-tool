plugins {
    alias(libs.plugins.bratek20.library.conventions)
}

dependencies {
    implementation("org.apache.velocity:velocity-engine-core:2.3") // TODO use version catalog

    //TODO add to convention
    testImplementation("org.junit.jupiter:junit-jupiter-params:${libs.versions.junit.get()}")
}

group = "pl.bratek20.hla"
version = "1.0.0-SNAPSHOT"