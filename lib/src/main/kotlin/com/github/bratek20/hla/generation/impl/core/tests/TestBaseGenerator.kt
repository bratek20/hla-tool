package com.github.bratek20.hla.generation.impl.core.tests

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.StatementBuilder
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.assignment
import com.github.bratek20.codebuilder.builders.functionCall
import com.github.bratek20.codebuilder.builders.functionCallStatement
import com.github.bratek20.codebuilder.builders.methodCallStatement
import com.github.bratek20.codebuilder.builders.parenthesis
import com.github.bratek20.codebuilder.builders.statement
import com.github.bratek20.codebuilder.builders.string
import com.github.bratek20.codebuilder.builders.variable
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.typeScriptArrowFunction
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.languages.typescript.typeScriptMultilineStructure
import com.github.bratek20.codebuilder.languages.typescript.typeScriptStructure
import com.github.bratek20.codebuilder.types.TypeBuilder
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.emptyImmutableList
import com.github.bratek20.codebuilder.types.nullCoalescing
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.fixtures.DefType
import com.github.bratek20.hla.generation.impl.core.fixtures.DefTypeFactory
import com.github.bratek20.hla.generation.impl.core.fixtures.ListDefType
import com.github.bratek20.utils.camelToScreamingSnakeCase

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
        val propertyKeyName: String,
        val defType: DefType<*>
    ) {
        val type: TypeBuilder
            get() = defType.builder()

        fun titleDataValue(): ExpressionBuilder {
            val argsValue = nullCoalescing {
                left = variable("args.$name")
                defaultValue = if (defType is ListDefType) {
                    emptyImmutableList(defType.wrappedType.builder())
                } else {
                    typeScriptStructure {}
                }
                parenthesizeDefaultValue = false
            }

            return if (defType is ListDefType) {
                defType.modernBuild(parenthesis(argsValue))
            } else {
                defType.modernBuild(argsValue)
            }
        }
    }

    private fun setupArgsFields(): List<SetupArgsField> {
        val defTypeFactory = DefTypeFactory(language.buildersFixture())
        return module.getPropertyKeys().map { key ->
            SetupArgsField(
                name = key.getName().replaceFirstChar { it.lowercase() },
                propertyKeyName = camelToScreamingSnakeCase(key.getName() + "PropertyKey"),
                defType = defTypeFactory.create(apiTypeFactory.create(key.getType()))
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
                    if (fields.isEmpty()) {
                        add(emptyContextAssignment())
                    } else {
                        add(setupAndCreateContextAssignment(fields))
                    }
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

    private fun emptyContextAssignment(): StatementBuilder = assignment {
        left = variable(CONTEXT_NAME)
        right = functionCall {
            name = "EmptyContextFor"
            addArg {
                dependencyName()
            }
        }
    }

    private fun setupAndCreateContextAssignment(fields: List<SetupArgsField>): StatementBuilder {
        val setupArgs = typeScriptMultilineStructure {
            addProperty {
                key = "dependencyName"
                value = dependencyName()
            }
            addProperty {
                key = "titleData"
                blockValue = typeScriptArrowFunction {
                    argName = TITLE_DATA_BUILDER_NAME
                    setBody {
                        fields.forEach { field ->
                            add(methodCallStatement {
                                target = variable(TITLE_DATA_BUILDER_NAME)
                                name = "with"
                                addArg {
                                    variable(field.propertyKeyName)
                                }
                                addArg {
                                    field.titleDataValue()
                                }
                            })
                        }
                    }
                }
            }
        }

        return statement {{
            lineStart()
            add(variable(CONTEXT_NAME))
            linePart(" = ")
            linePart("Ts.E2E.SetupAndCreateContext(")
            add(setupArgs)
            lineSoftEnd(").context")
        }}
    }

    private fun dependencyName(): ExpressionBuilder {
        return variable("DependencyName.$moduleName")
    }

    companion object {
        private const val SETUP_ARGS_NAME = "SetupArgs"
        private const val CONTEXT_NAME = "context"
        private const val TITLE_DATA_BUILDER_NAME = "builderTD"
    }
}