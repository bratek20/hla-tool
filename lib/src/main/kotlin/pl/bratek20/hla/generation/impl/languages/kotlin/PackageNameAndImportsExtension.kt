package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ContentBuilderExtension
import pl.bratek20.hla.generation.impl.core.DomainContext
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

fun srcPathToRootPackage(srcPath: String): String {
    return srcPath.replace("/", ".")
}

class PackageNameAndImportsExtension(
    private val c: DomainContext
) : ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        val rootPackage = srcPathToRootPackage(c.profile.srcPath);

        val imports = c.modules.getCurrentDependencies()
            .map { "$rootPackage.${it.value.lowercase()}" }


        builder
            .put("packageName", "$rootPackage.${c.module.name.value.lowercase()}")
            .put("imports", imports)
    }
}