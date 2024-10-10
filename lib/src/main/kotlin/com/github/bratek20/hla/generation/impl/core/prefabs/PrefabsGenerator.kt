package com.github.bratek20.hla.generation.impl.core.prefabs

import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator

class PrefabsGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Prefabs
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return c.language.name() == ModuleLanguage.C_SHARP
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            PrefabBlueprintsGenerator(),
        )
    }
}