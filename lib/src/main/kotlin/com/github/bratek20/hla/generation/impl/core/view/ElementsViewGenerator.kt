package com.github.bratek20.hla.generation.impl.core.view

import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.viewmodel.BaseViewModelPatternGenerator

class ElementsViewGenerator: BaseViewModelPatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.ElementsView
    }

    override fun shouldGenerate(): Boolean {
        return viewModelElementsDef().isNotEmpty()
    }
}