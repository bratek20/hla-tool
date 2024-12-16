package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.apitypes.impl.ComplexValueObjectApiType
import com.github.bratek20.hla.apitypes.impl.EventApiType

class EventsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Events
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.KOTLIN || c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getEvents().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        module.getEvents().map {
            apiTypeFactory.create<EventApiType>(it)
        }.forEach {
            addClass(it.getClassOps())
        }
    }

    override fun extraKotlinImports(): List<String> = listOf(
        "com.github.bratek20.architecture.events.api.Event"
    )
}