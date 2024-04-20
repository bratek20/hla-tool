package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.domain.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.ModuleGenerationContext
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class PackageNameExtension(
    c: ModuleGenerationContext
) : ContentBuilderExtension(c) {
    override fun extend(builder: VelocityFileContentBuilder) {
        val imports = c.modules.getDependencies(c.name)
            .map { "pl.bratek20.${it.value.lowercase()}" }

        builder
            .put("packageName", "pl.bratek20.${c.name.value.lowercase()}")
            .put("imports", imports)
    }
}