package com.github.bratek20.hla.generation.context

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.hla.generation.api.ModuleGenerator
import com.github.bratek20.hla.generation.impl.core.ModuleGeneratorLogic
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiGenerator
import com.github.bratek20.hla.generation.impl.core.api.patterns.ValueObjectsGenerator
import com.github.bratek20.hla.generation.impl.core.prefabs.PrefabBlueprintsGenerator
import com.github.bratek20.hla.generation.impl.core.prefabs.PrefabsGenerator
import com.github.bratek20.hla.generation.impl.core.view.ElementsViewGenerator
import com.github.bratek20.hla.generation.impl.core.view.ViewGenerator
import com.github.bratek20.hla.generation.impl.core.viewmodel.*
import com.github.bratek20.hla.generation.impl.core.web.WebGenerator
import com.github.bratek20.hla.generation.impl.core.web.WebServerContextGenerator
import com.github.bratek20.hla.hlatypesworld.context.HlaTypesWorldImpl
import com.github.bratek20.hla.importscalculation.context.ImportsCalculationImpl
import com.github.bratek20.hla.mvvmtypesmappers.context.MvvmTypesMappersImpl
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.velocity.context.VelocityImpl

class GenerationImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(ModuleGenerator::class.java, ModuleGeneratorLogic::class.java)
            .withModules(
                VelocityImpl(),
                TypesWorldImpl(),
                HlaTypesWorldImpl(),
                MvvmTypesMappersImpl(),
                ImportsCalculationImpl(),

                WebGenerators(),
                ViewModelGenerators(),
                ViewGenerators(),
            )
            .addClass(PrefabsGenerator::class.java)
            .addClass(PrefabBlueprintsGenerator::class.java)
            .addClass(ApiGenerator::class.java)
            .addImpl(PatternGenerator::class.java, ValueObjectsGenerator::class.java)
    }
}

private class WebGenerators: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .addClass(WebGenerator::class.java)
            .addImpl(PatternGenerator::class.java, WebServerContextGenerator::class.java)
    }
}

private class ViewModelGenerators: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .addClass(ViewModelGenerator::class.java)
            .addImpl(PatternGenerator::class.java, GeneratedElementsGenerator::class.java)
            .addImpl(PatternGenerator::class.java, ElementsLogicGenerator::class.java)
            .addImpl(PatternGenerator::class.java, GeneratedPopupsGenerator::class.java)
            .addImpl(PatternGenerator::class.java, GeneratedWindowsGenerator::class.java)
            .addImpl(PatternGenerator::class.java, ViewModelContextGenerator::class.java)
    }
}

private class ViewGenerators: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .addClass(ViewGenerator::class.java)
            .addImpl(PatternGenerator::class.java, ElementsViewGenerator::class.java)
    }
}