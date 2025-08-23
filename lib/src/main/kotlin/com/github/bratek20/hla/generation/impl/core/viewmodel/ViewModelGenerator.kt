package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator

class ViewModelGenerator(
    private val generatedElementsGenerator: GeneratedElementsGenerator,
    private val elementsLogicGenerator: ElementsLogicGenerator,
    private val generatedPopupsGenerator: GeneratedPopupsGenerator,
    private val generatedWindowsGenerator: GeneratedWindowsGenerator,
    private val viewModelContextGenerator: ViewModelContextGenerator
): SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.ViewModel
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            generatedElementsGenerator,
            elementsLogicGenerator,
            generatedWindowsGenerator,
            WindowsLogicGenerator(),
            generatedPopupsGenerator,
            PopupsLogicGenerator(),
            viewModelContextGenerator
        )
    }
}