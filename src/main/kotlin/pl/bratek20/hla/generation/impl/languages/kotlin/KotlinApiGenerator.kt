package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ApiGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade
import pl.bratek20.hla.velocity.api.VelocityFileContentBuilder

class KotlinApiGenerator(module: HlaModule, velocity: VelocityFacade)
    : ApiGenerator(module, velocity, KotlinTypes()) {

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