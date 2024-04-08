package pl.bratek20.hla.generation.impl

import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

fun contentBuilder(
    velocity: VelocityFacade,
    templatePath: String,
    moduleName: String,
): VelocityFileContentBuilder {
    return velocity.contentBuilder(templatePath)
        .put("packageName", "pl.bratek20.${moduleName.lowercase()}")
}