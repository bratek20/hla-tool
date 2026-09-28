package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.AccessModifier
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class EnumValuesGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.EnumValues
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        if (language.name() == ModuleLanguage.C_SHARP) {
            return false
        }
        return module.getEnumValues().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        module.getEnumValues().forEach { definition ->
            val populatedType = definition.getPopulates()
            addClass {
                name = definition.getName()
                definition.getValues().forEach { enumValue ->
                    addField {
                        modifier = AccessModifier.PUBLIC
                        static = true
                        name = enumValue
                        value = constructorCall {
                            className = populatedType
                            addArg {
                                string(enumValue)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}
