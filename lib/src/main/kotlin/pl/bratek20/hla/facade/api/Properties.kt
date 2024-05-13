package pl.bratek20.hla.facade.api

val PROPERTIES_KEY = pl.bratek20.architecture.properties.api.PropertyKey("properties")

data class KotlinProperties(
    val rootPackage: String,
) {
}
data class TypeScriptProperties(
    val srcPath: String,
    val testPath: String,
) {
}
data class HlaProperties(
    val generateWeb: Boolean,
    val kotlin: KotlinProperties,
    val typeScript: TypeScriptProperties,
) {
}