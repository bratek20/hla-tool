package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.hla.apitypes.impl.EventApiType
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.PerFileOperations

class EventsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Events
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getEvents().isNotEmpty()
    }

    override fun getOperationsPerFile(): List<PerFileOperations> {
        return listOf(
            PerFileOperations(
                fileName = if (language.name() == ModuleLanguage.TYPE_SCRIPT)
                        "Notifications" else "Events",
                ops = {
                    module.getEvents().map {
                        apiTypeFactory.create<EventApiType>(it)
                    }.forEach {
                        addClass(it.getClassOps())
                    }
                }
            )
        )
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }

    override fun extraKotlinImports(): List<String> = listOf(
        "com.github.bratek20.architecture.events.api.Event"
    )
}