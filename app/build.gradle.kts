plugins {
    id("application")
}

dependencies {
    implementation(project(":lib"))
}

application {
    mainClass = "pl.bratek20.hla.app.Main"
}