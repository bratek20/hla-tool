package com.github.bratek20.hla.generation.impl.core.context

import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class ContextGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Context
    }

    override fun velocityDirPath(): String {
        return "context"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            ImplContextGenerator(),
            WebContextGenerator(),
            ViewModelContextGenerator()
        )
    }
}