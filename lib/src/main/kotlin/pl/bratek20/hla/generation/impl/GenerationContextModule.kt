package pl.bratek20.hla.generation.impl

import pl.bratek20.architecture.context.api.ContextBuilder
import pl.bratek20.architecture.context.api.ContextModule
import pl.bratek20.hla.generation.api.ModuleGenerator
import pl.bratek20.hla.generation.impl.core.ModuleGeneratorImpl

class GenerationContextModule: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleGenerator::class.java, ModuleGeneratorImpl::class.java)
    }
}