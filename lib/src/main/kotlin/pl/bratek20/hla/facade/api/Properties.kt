package pl.bratek20.hla.facade.api

import pl.bratek20.hla.directory.api.*

val PROPERTIES_KEY = pl.bratek20.architecture.properties.api.PropertyKey("properties")

data class HlaProfile(
    private val name: String,
    val language: ModuleLanguage,
    val projectPath: String,
    val srcPath: String,
    val fixturesPath: String,
    val onlyParts: List<String>,
) {
    fun getName(): ProfileName {
        return ProfileName(this.name)
    }
}
data class HlaProperties(
    val profiles: List<HlaProfile>,
) {
}