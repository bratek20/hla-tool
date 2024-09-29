package com.github.bratek20.hla.generation.impl.languages.kotlin

import com.github.bratek20.hla.facade.api.HlaProfile
import com.github.bratek20.hla.generation.impl.core.ContentBuilderExtension
import com.github.bratek20.hla.generation.impl.core.DomainContext
import com.github.bratek20.hla.velocity.api.VelocityFileContentBuilder

fun profileToRootPackage(profile: HlaProfile): String {
    val defaultPath = profile.getPaths().getSrc().getDefault()
    return defaultPath.value
        .replace("src/main/kotlin/", "")
        .replace("src/main/java/", "")
        .replace("/", ".")
}

class PackageNameAndImportsExtension(
    private val c: DomainContext
) : ContentBuilderExtension {
    override fun extend(builder: VelocityFileContentBuilder) {
        val imports = c.queries.getCurrentDependencies()
            .map {
                val rootPackage = profileToRootPackage(it.getGroup().getProfile())
                "$rootPackage.${it.getModule().getName().value.lowercase()}"
            }


        val rootPackage = profileToRootPackage(c.profile)
        builder
            .put("packageName", "$rootPackage.${c.module.getName().value.lowercase()}")
            .put("imports", imports)
    }
}