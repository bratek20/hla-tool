plugins {
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("application")
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.bratek20.architecture)
    implementation("com.github.bratek20.logs:logs-logback:1.0.5")
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