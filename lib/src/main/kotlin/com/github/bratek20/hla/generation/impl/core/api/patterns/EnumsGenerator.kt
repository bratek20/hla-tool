package com.github.bratek20.hla.generation.impl.core.api.patterns

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.legacyConstructorCall
import com.github.bratek20.codebuilder.builders.legacyString
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class EnumsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Enums
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getEnums().isNotEmpty()
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        if (language.name() == ModuleLanguage.TYPE_SCRIPT) {
            module.getEnums().forEach {
                val enumName = it.getName()
                addClass {
                    name = enumName
                    extends {
                        className = "StringEnumClass"
                    }
                    it.getValues().forEach {
                        addField {
                            name = it
                            static = true
                            legacyValue = { legacyConstructorCall {
                                className = enumName
                                addArgLegacy {
                                    legacyString(it)
                                }
                            } }
                        }
                    }
                }
            }
        }
        else {
            module.getEnums().forEach {
                addEnum {
                    name = it.getName()
                    it.getValues().forEach { addValue(it) }
                }
            }
        }
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}