package com.github.bratek20.hla.generation.impl.languages.kotlin

import com.github.bratek20.hla.directory.api.Path
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder

private fun srcPathToRootPackage(mainPath: Path): String {
    return mainPath.value
        .replace("src/main/kotlin/", "")
        .replace("src/main/java/", "")
        .replace("/", ".")
}

class PackageNameAndImportsExtension(
    private val c: DomainContext
) : ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        val rootPackage = srcPathToRootPackage(c.profile.getPaths().getSrc().getMain());

        val imports = c.queries.getCurrentDependencies()
            .map { "$rootPackage.${it.value.lowercase()}" }


        builder
            .put("packageName", "$rootPackage.${c.module.getName().value.lowercase()}")
            .put("imports", imports)
    }
}