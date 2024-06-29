plugins {
    alias(libs.plugins.bratek20.internal.kotlin.library.conventions)
}

dependencies {
    implementation(libs.assertj.core)

    implementation(libs.bratek20.architecture)
    implementation(libs.bratek20.infrastructure)

    //TODO-REF introduce libs.bratek20.infrastructure.web.server that has it as api dependency
    implementation(platform(libs.spring.boot.dependencies))
    implementation("org.springframework:spring-web")
}