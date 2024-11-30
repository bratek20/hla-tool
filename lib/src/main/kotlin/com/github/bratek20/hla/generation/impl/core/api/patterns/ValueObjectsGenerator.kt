package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.apitypes.impl.ComplexValueObjectApiType
import com.github.bratek20.hla.apitypes.impl.SimpleValueObjectApiType
import com.github.bratek20.utils.directory.api.FileContent

class ValueObjectsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ValueObjects
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return module.getSimpleValueObjects().isNotEmpty() || module.getComplexValueObjects().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val simpleVOs = module.getSimpleValueObjects().map {
            apiTypeFactory.create<SimpleValueObjectApiType>(it)
        }
        val complexVOs = module.getComplexValueObjects().map {
            apiTypeFactory.create<ComplexValueObjectApiType>(it)
        }
        simpleVOs.forEach {
            addClass(it.getClassOps())
        }
        complexVOs.forEach {
            addClass(it.getClassOps())
        }
    }

    override fun generateFileContent(): FileContent? {
        val simpleValueObjects = module.getSimpleValueObjects().map { apiTypeFactory.create<SimpleValueObjectApiType>(it) }
        val complexValueObjects = module.getComplexValueObjects().map { apiTypeFactory.create<ComplexValueObjectApiType>(it) }

        if (simpleValueObjects.isEmpty() && complexValueObjects.isEmpty()) {
            return null
        }

        return contentBuilder("valueObjects.vm")
            .put("simpleValueObjects", simpleValueObjects)
            .put("complexValueObjects", complexValueObjects)
            .build()
    }


}