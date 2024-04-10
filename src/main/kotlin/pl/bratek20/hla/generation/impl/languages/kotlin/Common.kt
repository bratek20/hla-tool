package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

fun kotlinContentBuilder(
    velocity: VelocityFacade,
    templateName: String,
    moduleName: String,
): VelocityFileContentBuilder {
    return velocity.contentBuilder("templates/kotlin/$templateName")
        .put("packageName", "pl.bratek20.${moduleName.lowercase()}")
}