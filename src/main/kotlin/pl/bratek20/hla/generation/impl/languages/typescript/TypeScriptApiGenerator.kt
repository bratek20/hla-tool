package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ModulePartGeneratorContext
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class TypeScriptApiGenerator(c: ModulePartGeneratorContext)
    : ApiGenerator(c, TypeScriptTypes()) {

    override fun dirName(): String {
        return "Api"
    }

    override fun valueObjectsFileName(): String {
        return "ValueObjects.ts"
    }

    override fun valueObjectsContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "valueObjects.vm", module.name)
    }

    override fun interfacesFileName(): String {
        return "Interfaces.ts"
    }

    override fun interfacesContentBuilder(): VelocityFileContentBuilder {
        return typeScriptContentBuilder(velocity, "interfaces.vm", module.name)
    }
}