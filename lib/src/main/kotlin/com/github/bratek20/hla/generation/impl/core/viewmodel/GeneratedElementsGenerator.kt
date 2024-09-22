package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class GeneratedElementsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedElements
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getViewModelSubmodule()?.getElements()?.isNotEmpty() ?: false
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {

    }
}