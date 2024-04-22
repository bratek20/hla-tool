package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class PackageNameExtension(
    private val c: DomainContext
) : ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        val imports = c.modules.getCurrentDependencies()
            .map { "pl.bratek20.${it.value.lowercase()}" }

        builder
            .put("packageName", "pl.bratek20.${c.module.name.value.lowercase()}")
            .put("imports", imports)
    }
}