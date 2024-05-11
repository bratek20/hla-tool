package pl.bratek20.hla.facade.api

val PROPERTIES_KEY = pl.bratek20.architecture.properties.api.PropertyKey("properties")

data class JavaProperties(
    val rootPackage: String,
) {
}
data class HlaProperties(
    val generateWeb: Boolean,
    val java: JavaProperties,
) {
}