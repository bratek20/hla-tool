package com.github.bratek20.hla.generation.impl.core.menu

import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.examples.patterns.ExampleKeyDefinitionLogic
import com.github.bratek20.hla.generation.impl.core.menu.patterns.MenuPatternGenerator
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName

class MenuGenerator: SubmoduleGenerator() {


    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Menu
    }

    override fun velocityDirPath(): String {
        return "Menu"
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
                MenuPatternGenerator()
        )
    }


}
