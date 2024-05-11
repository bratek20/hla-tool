package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.domain.DomainContext
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class PackageNameAndImportsExtension(
    private val c: DomainContext
) : ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        val rootPackage = c.properties.java.rootPackage;

        val imports = c.modules.getCurrentDependencies()
            .map { "$rootPackage.${it.value.lowercase()}" }


        builder
            .put("packageName", "$rootPackage.${c.module.name.value.lowercase()}")
            .put("imports", imports)
    }
}