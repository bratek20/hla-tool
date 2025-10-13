package com.github.bratek20.hla.generation.impl.core.menu

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.utils.directory.api.Directory
import com.github.bratek20.utils.directory.api.DirectoryName

class MenuPattern: PatternGenerator() {

    val LIBRARY_PREFIX_VAR_NAME = "libraryPrefix"
    val BUILDER_VAR_NAME = "builder"

    override fun patternName(): PatternName {
        return PatternName.Menu
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val interfacesMethods = getExposedMethods()

        addInitFunction(this, interfacesMethods)
        addInterfacesFunctions(this, interfacesMethods)
    }

    private fun getExposedMethods(): List<MethodDefinition> {
        return module.getMenuSubmodule()?.getExposedInterfaces()
                ?.flatMap { exposedInterface ->
                    module.getInterfaces()
                            .firstOrNull { it.getName() == exposedInterface }
                            ?.getMethods()
                            ?: emptyList()
                }
                ?: emptyList()
    }

    private fun addInitFunction(
            builder: TopLevelCodeBuilder,
            methods: List<MethodDefinition>
    ) {
        with(builder) {
            addFunction {
                name = "init${module.getName()}Menu"
                addArg { name = LIBRARY_PREFIX_VAR_NAME; type = softOptionalType(baseType(BaseType.STRING)) }

                setBody {
                    add(createBuilderAssignment())
                    add(createNamespacesAddingChain(methods))
                }
            }
        }
    }

    private fun createBuilderAssignment(): StatementBuilder =
            assignment {
                left = variableDeclaration { name = BUILDER_VAR_NAME }
                right = constructorCall {
                    className = "MenuBuilder"
                    addArg { string(module.getName().value) }
                    addArg { variable(LIBRARY_PREFIX_VAR_NAME) }
                }
            }

    private fun createNamespacesAddingChain(
            methods: List<MethodDefinition>
    ): StatementBuilder = methodCallStatement {
        target = methods.fold(variable(BUILDER_VAR_NAME)) { current, method ->
            methodCall {
                target = current
                methodName = "addNamespaced"
                addArg { string(camelToHumanReadableCase(method.getName())) }
                addArg { string("${module.getName()}.Menu") }
                addArg { variable(method.getName()) }
            }
        }
        methodName = "build"
    }

    private fun addInterfacesFunctions(
            builder: TopLevelCodeBuilder,
            methods: List<MethodDefinition>
    ) {
        with(builder) {
            methods.forEach { methodName ->
                addFunction(createMenuFunction(methodName))
            }
        }
    }

    private fun createMenuFunction(
            methods: MethodDefinition
    ): FunctionBuilder.() -> Unit = {
        name = methods.getName()
        setBody {
            add(createMenuDecoratorCall(methods))
        }
    }

    private fun createMenuDecoratorCall(
            method: MethodDefinition
    ): StatementBuilder = methodCallStatement {
        this.target = variable("Woh")
        this.methodName = "menuDecorator"
        addArg {
            lambda {
                addArg { name = "c"; type = baseType(BaseType.ANY) }
                body = methodCall {
                    this.target = variable("${module.getName()}.Api.${method.getName()}")
                    this.methodName = method.getName()
                    addArg { variable("c") }
                }
            }
        }
    }
    override fun supportsCodeBuilder() = true

    override fun shouldGenerate(): Boolean {
        return c.module.getMenuSubmodule() != null && c.language.name() == ModuleLanguage.TYPE_SCRIPT
    }

    override fun getDirectory(): Directory? {
        return Directory.create(
            name = DirectoryName("Menu")
        )
    }

    private fun camelToHumanReadableCase(name: String = "example"): String {
        return name.replace(Regex("([a-z])([A-Z])"), "$1 $2").replaceFirstChar { it.uppercase() }
    }
}