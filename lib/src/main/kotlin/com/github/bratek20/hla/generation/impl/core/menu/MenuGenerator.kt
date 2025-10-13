package com.github.bratek20.hla.generation.impl.core.menu

import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator

class MenuGenerator: SubmoduleGenerator() {


    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Menu
    }

    override fun velocityDirPath(): String {
        return "Menu"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
                MenuPattern()
        )
    }


}
