package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

fun typeScriptContentBuilder(
    velocity: VelocityFacade,
    templateName: String,
    moduleName: String,
): VelocityFileContentBuilder {
    return velocity.contentBuilder("templates/typescript/$templateName")
        .put("moduleName", moduleName)
}