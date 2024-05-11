package pl.bratek20.hla.generation.context

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorLogic

class GenerationImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleGenerator::class.java, ModuleGeneratorLogic::class.java)
    }
}