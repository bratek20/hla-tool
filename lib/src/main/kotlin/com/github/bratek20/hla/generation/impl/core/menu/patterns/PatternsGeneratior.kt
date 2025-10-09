package com.github.bratek20.hla.generation.impl.core.menu.patterns

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.definitions.api.KeyDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.examples.patterns.ExampleKeyDefinitionLogic
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName
import kotlin.reflect.jvm.internal.impl.metadata.ProtoBuf

class MenuPatternGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Menu
    }

    protected fun getKeys(): List<KeyDefinition> {
        return module.getPropertyKeys()
    }

    override fun getOperations(): TopLevelCodeBuilderOps {
        return {

            var methodNames = mutableListOf<String>()

            module.getInterfaces().forEach {
                it.getMethods().forEach {method ->
                    methodNames.add(method.getName())
                }
            }

            addFunction {
                name = "init${module.getName()}Menu"
                addArg { name = "libraryPrefix"; type = softOptionalType(baseType(BaseType.STRING)) }
                setBody {
                    add(assignment {
                        left = variableDeclaration {
                            name = "builder"
                        }
                        right = constructorCall {
                            className = "MenuBuilder"
                            addArg {
                                string(module.getName().value)
                            }
                            addArg {
                                variable("libraryPrefix")
                            }
                        }
                    })
                }
            }

            methodNames.forEach {methodName ->
                addFunction {
                    name = methodName
                }
            }
        }
    }

    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.module.getMenuSubmodule() != null
    }

    override fun getDirectory(): Directory? {
        return Directory.create(
            name = DirectoryName("Menu")
        )
    }
}