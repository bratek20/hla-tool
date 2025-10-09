package com.github.bratek20.hla.generation.impl.core.menu.patterns

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.examples.patterns.ExampleKeyDefinitionLogic
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName

class MenuPatternGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Menu
    }

    protected fun getKeys(): List<KeyDefinition> {
        return module.getPropertyKeys()
    }

    override fun getOperations(): TopLevelCodeBuilderOps {
        return {

            module.getInterfaces().forEach {
                addFunction {
                    name = it.getName()
                }

            }
        }
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT && getKeys().isNotEmpty()
    }

    override fun getDirectory(): Directory? {
        return Directory.create(
            name = DirectoryName("Menu")
        )
    }
}