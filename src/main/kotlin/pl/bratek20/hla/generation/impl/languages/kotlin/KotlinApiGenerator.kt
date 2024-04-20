package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.domain.ModuleName
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class KotlinApiGenerator(c: ModulePartGeneratorContext)
    : ApiGenerator(c, KotlinTypes()) {

    override fun dirName(): String {
        return "api"
    }

    override fun valueObjectsFileName(): String {
        return "ValueObjects.kt"
    }

    override fun valueObjectsContentBuilder(): VelocityFileContentBuilder {
        return kotlinContentBuilder(velocity, "valueObjects.vm", module.name)
    }

    override fun interfacesFileName(): String {
        return "Interfaces.kt"
    }

    override fun interfacesContentBuilder(): VelocityFileContentBuilder {
        return kotlinContentBuilder(velocity, "interfaces.vm", module.name)
    }
}