package pl.bratek20.hla.facade.api

data class JavaProperties(
    val rootPackage: String,
)

data class HlaProperties(
    val java: JavaProperties,
)