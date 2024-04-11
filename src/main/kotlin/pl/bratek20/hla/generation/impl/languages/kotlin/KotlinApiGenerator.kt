package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.ApiGenerator
import pl.bratek20.hla.model.HlaModule
import pl.bratek20.hla.velocity.api.VelocityFacade

class KotlinApiGenerator(module: HlaModule, velocity: VelocityFacade)
    : ApiGenerator(module, velocity, KotlinTypes()) {

    override fun dirName(): String {
        return "api"
    }
}