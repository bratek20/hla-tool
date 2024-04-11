package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.ApiGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade

class TypeScriptApiGenerator(module: HlaModule, velocity: VelocityFacade)
    : ApiGenerator(module, velocity, TypeScriptTypes()) {

    override fun dirName(): String {
        return "Api"
    }
}