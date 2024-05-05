plugins {
    id("application")
}

dependencies {
    implementation(project(":lib"))

    implementation(libs.bratek20.architecture)
}

application {
    mainClass = "pl.bratek20.hla.app.Main"
}