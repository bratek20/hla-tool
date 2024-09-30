package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator

class ViewGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.View
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            ElementsViewGenerator()
        )
    }
}