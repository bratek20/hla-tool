package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class GeneratedWindowsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedWindows
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    private fun viewModelWindows() = module.getViewModelSubmodule()?.getWindows()

    override fun shouldGenerate(): Boolean {
        return viewModelWindows()?.isNotEmpty() ?: false
    }
}