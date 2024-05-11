package pl.bratek20.hla.facade.api

import pl.bratek20.architecture.properties.api.PropertyKey

data class JavaProperties(
    val rootPackage: String,
)

val HLA_PROPERTIES_KEY = PropertyKey("properties")

data class HlaProperties(
    val java: JavaProperties,
)