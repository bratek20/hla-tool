package com.github.bratek20.hla.generation.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGeneratorLogic
import com.github.bratek20.hla.generation.impl.core.prefabs.PrefabBlueprintsGenerator
import com.github.bratek20.hla.generation.impl.core.prefabs.PrefabsGenerator
import com.github.bratek20.hla.hlatypesworld.context.HlaTypesWorldImpl
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.velocity.context.VelocityImpl

class GenerationImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(ModuleGenerator::class.java, ModuleGeneratorLogic::class.java)
            .withModules(
                VelocityImpl(),
                TypesWorldImpl(),
                HlaTypesWorldImpl()
            )
            .addClass(PrefabsGenerator::class.java)
            .addClass(PrefabBlueprintsGenerator::class.java)
    }
}