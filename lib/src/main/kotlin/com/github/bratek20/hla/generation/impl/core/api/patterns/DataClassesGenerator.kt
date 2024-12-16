package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.hla.apitypes.impl.DataClassApiType
import com.github.bratek20.hla.definitions.api.ComplexStructureDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.FileContent

open class DataClassesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.DataClasses
    }

    override fun supportsCodeBuilder(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun shouldGenerate(): Boolean {
        return dataClasses().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val dataClasses = dataClasses().map {
            apiTypeFactory.create<DataClassApiType>(it)
        }
        dataClasses.forEach {
            addClass(it.getClassOps())
        }
    }

    protected open fun dataClasses(): List<ComplexStructureDefinition> {
        return module.getDataClasses()
    }

    override fun generateFileContent(): FileContent? {
        val dataClasses = dataClasses().map { apiTypeFactory.create<DataClassApiType>(it) }

        if (dataClasses.isEmpty()) {
            return null
        }

        return contentBuilder("dataClasses.vm")
            .put("dataClasses", dataClasses)
            .build()
    }
}