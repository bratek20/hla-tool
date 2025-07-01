package com.github.bratek20.hla.generation.impl.core.examples

import com.github.bratek20.hla.generation.impl.core.examples.patterns.HandlersExamplesGenerator
import com.github.bratek20.hla.generation.impl.core.examples.patterns.PlayerDataExamplesGenerator
import com.github.bratek20.hla.generation.impl.core.examples.patterns.TitleDataExamplesGenerator
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator


class ExamplesGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Examples
    }

    override fun velocityDirPath(): String {
        return "Examples"
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return module.getWebSubmodule() != null || module.getDataKeys().isNotEmpty() || module.getPropertyKeys().isNotEmpty()
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            TitleDataExamplesGenerator(),
            PlayerDataExamplesGenerator(),
            HandlersExamplesGenerator(),
        )
    }
}