package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class PackageNameExtension(
    c: ModuleGenerationContext
) : ContentBuilderExtension(c) {
    override fun extend(builder: VelocityFileContentBuilder) {
        builder
            .put("packageName", "pl.bratek20.${c.name.value.lowercase()}")
            .put("imports", listOf<String>())
    }
}