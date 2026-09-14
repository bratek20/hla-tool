plugins {
    alias(libs.plugins.b20.library)
}

dependencies {
    api(libs.bratek20.architecture)
    api(libs.bratek20.infrastructure)

    //TODO-REF introduce libs.bratek20.infrastructure.web.server that has it as api dependency
    implementation("org.springframework:spring-web")
}
