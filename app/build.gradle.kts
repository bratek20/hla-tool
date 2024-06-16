plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.bratek20.architecture)
    implementation(libs.bratek20.utils)

    implementation(libs.logback.classic) // to silence no provider message
}

application {
    mainClass.set("pl.bratek20.hla.app.Main")
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "pl.bratek20.hla.app.Main")
    }
}