package com.github.bratek20.hla.generation.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGeneratorLogic

class GenerationImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder.setImpl(ModuleGenerator::class.java, ModuleGeneratorLogic::class.java)
    }
}