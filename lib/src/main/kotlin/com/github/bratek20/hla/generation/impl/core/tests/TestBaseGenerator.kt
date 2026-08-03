package com.github.bratek20.hla.generation.impl.core.tests

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.assignment
import com.github.bratek20.codebuilder.builders.functionCall
import com.github.bratek20.codebuilder.builders.functionCallStatement
import com.github.bratek20.codebuilder.builders.string
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.languages.typescript.typeScriptStructure
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.fixtures.DefTypeFactory

class TestBaseGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.TestBase
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun shouldGenerate(): Boolean {
        return language.name() == ModuleLanguage.TYPE_SCRIPT
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }

    private class SetupArgsField(
        val name: String,
        val type: TypeBuilder
    )

    private fun setupArgsFields(): List<SetupArgsField> {
        val defTypeFactory = DefTypeFactory(language.buildersFixture())
        return module.getPropertyKeys().map { key ->
            SetupArgsField(
                name = key.getName().replaceFirstChar { it.lowercase() },
                type = defTypeFactory.create(apiTypeFactory.create(key.getType())).builder()
            )
        }
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val fields = setupArgsFields()

        add(typeScriptNamespace {
            name = moduleName

            addVariable {
                name = "context"
                type = typeName("HandlerContext")
                mutable = true
            }

            if (fields.isNotEmpty()) {
                addInterface {
                    name = SETUP_ARGS_NAME
                    fields.forEach { field ->
                        addField {
                            name = field.name
                            type = field.type
                            optional = true
                        }
                    }
                }
            }

            addFunction {
                name = "setup"
                returnType = baseType(BaseType.VOID)

                if (fields.isNotEmpty()) {
                    addArg {
                        name = "args"
                        type = typeName(SETUP_ARGS_NAME)
                        defaultValue = typeScriptStructure {}
                    }
                }

                setBody {
                    add(assignment {
                        left = variable("context")
                        right = functionCall {
                            name = "EmptyContextFor"
                            addArg {
                                variable("DependencyName.$moduleName")
                            }
                        }
                    })
                }
            }

            addFunction {
                name = "test"
                addArg {
                    name = "testName"
                    type = baseType(BaseType.STRING)
                }
                addArg {
                    name = "fun"
                    type = typeName("TestFunction")
                }

                setBody {
                    add(functionCallStatement {
                        name = "addTest"
                        addArg {
                            string(moduleName)
                        }
                        addArg {
                            variable("testName")
                        }
                        addArg {
                            variable("fun")
                        }
                    })
                }
            }
        })
    }

    companion object {
        private const val SETUP_ARGS_NAME = "SetupArgs"
    }
}