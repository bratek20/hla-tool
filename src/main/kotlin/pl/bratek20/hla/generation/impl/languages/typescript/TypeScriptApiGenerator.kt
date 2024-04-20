package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ModuleGenerationContext
import pl.bratek20.hla.generation.impl.core.api.ApiGenerator
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class TypeScriptApiGenerator(c: ModuleGenerationContext)
    : ApiGenerator(c, TypeScriptTypes()) {

    override fun dirName(): String {
        return "Api"
    }

    override fun valueObjectsFileName(): String {
        return "ValueObjects.ts"
    }

    override fun interfacesFileName(): String {
        return "Interfaces.ts"
    }
}