package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.*
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.*
import com.github.bratek20.hla.apitypes.api.ApiType
import com.github.bratek20.hla.apitypes.api.ApiTypeFactory
import com.github.bratek20.hla.apitypes.impl.ApiTypeLogic
import com.github.bratek20.hla.apitypes.impl.BaseApiType
import com.github.bratek20.hla.apitypes.impl.ExternalApiType
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.definitions.api.MethodDefinition
import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.languages.kotlin.profileToRootPackage
import com.github.bratek20.hla.generation.impl.languages.typescript.addModulePrefix
import com.github.bratek20.hla.generation.impl.languages.typescript.handleReferencing
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.hla.queries.api.methodsArgsTypeName
import com.github.bratek20.utils.camelToPascalCase

class MockMethodLogic(
    private val interfDef: InterfaceDefinition,
    private val def: MethodDefinition,
    private val apiTypeFactory: ApiTypeFactory,
    private val defTypeFactory: DefTypeFactory,
    private val expectedTypeFactory: ExpectedTypeFactory,
    private val languageName: ModuleLanguage,
    private val modules: ModuleGroupQueries
) {
    private val returnApiType = apiTypeFactory.create(def.getReturnType())
    private val returnDefType = defTypeFactory.create(returnApiType as ApiTypeLogic)


    fun mockedMethod(): MethodBuilderOps = {
        name = def.getName()
        this.overridesClassMethod = languageName == ModuleLanguage.KOTLIN

        def.getArgs().forEach { arg ->
            addArg {
                name = arg.getName()
                type = apiTypeFactory.create(arg.getType()).builder()
            }
        }
        val returnType = returnApiType
        this.returnType = returnType.builder()

        setBody {
            add(assignment {
                left = instanceVariable(callsNumberVariableName())
                right = plus {
                    left = instanceVariable(callsNumberVariableName())
                    right = const("1")
                }
            })

            if (def.getArgs().size == 1) {
                add(
                    callsListOp()
                        .add {
                            variable(def.getArgs()[0].getName())
                        }
                )
            }
            else if (def.getArgs().size > 1) {
                add(
                    callsListOp()
                        .add {
                            methodCall {
                                target = variable(methodsArgsTypeName(interfDef, def))
                                methodName = "create"
                                def.getArgs().forEach { arg ->
                                    addArg {
                                        variable(arg.getName())
                                    }
                                }
                            }
                        }
                )
            }

            if (!hasVoidReturnType()) {
                add(returnStatement {
                    defaultBuilderCall(returnType)
                })
            }
        }
    }

    private fun callsListOp(): ListOperations {
        return listOp(instanceVariable(callsVariableName()))
    }

    //calls number
    fun callsNumberField(): FieldBuilderOps {
        return {
            type = baseType(BaseType.INT)
            name = callsNumberVariableName()
            value = const("0")
            mutable = true
        }
    }

    fun callsNumberAssertion(): MethodBuilderOps = {
        name = assertCallsNumberMethodName()
        val expectedValue = "expectedNumber"
        val expectedValueExpression = variable(expectedValue)
        val givenExpression = instanceVariable(callsNumberVariableName())
        addArg {
            name = expectedValue
            type = baseType(BaseType.INT)
        }
        setBody {
            add(assertEquals {
                given = givenExpression
                expected = expectedValueExpression
                message = plus {
                    left = string("Expected '${def.getName()}' to be called ")
                    right = plus {
                        left = variable(expectedValue)
                        right = plus {
                            left = string(" times but was called ")
                            right = plus {
                                left = givenExpression
                                right = string(" times")
                            }
                        }
                    }
                }
            })
        }
    }

    private fun assertCallsNumberMethodName() =
        "assert${camelToPascalCase(def.getName())}CallsNumber"

    //calls
    fun callsField(): FieldBuilderOps? {
        return argsApiType()?.let {
            {
                type = mutableListType(it.builder())
                name = callsVariableName()
                value = emptyMutableList(it.builder())
                mutable = true
            }
        }
    }

    fun callsAssertion(): MethodBuilderOps? {
        return supportedArgsExpectedType()?.let {
            {
                val expectedArgsListOp = listOp(variable("expectedArgs"))
                name = "assert${camelToPascalCase(def.getName())}Calls"
                addArg {
                    name = "expectedArgs"
                    type = listType(typeName(it.name()))
                }
                setBody {
                    add(methodCallStatement {
                        methodName = assertCallsNumberMethodName()
                        addArg {
                            expectedArgsListOp.size()
                        }
                    })
                    add(forLoop(
                        from = const(0),
                        to = expectedArgsListOp.size(),
                        body = { iVar ->
                            functionCallStatement {
                                name = if (languageName == ModuleLanguage.TYPE_SCRIPT) handleTypeScriptReferencing(it) else it.funName()
                                addArg {
                                    callsListOp().get(iVar)
                                }
                                addArg {
                                    expectedArgsListOp.get(iVar)
                                }
                            }
                        }
                    ))
                }
            }
        }
    }

    private fun handleTypeScriptReferencing(expectedType: ExpectedType<*>): String {
        return addModulePrefix(modules, expectedType.api.name(), expectedType.funName(), "Assert")
    }

    //response
    fun responseField(): FieldBuilderOps {
        return {
            type = returnDefType.builder()
            name = responseFieldName()
            value = returnDefType.emptyValueBuilder()
            mutable = true
        }
    }

    fun setResponse(): MethodBuilderOps = {
        name = "set${camelToPascalCase(def.getName())}Response"
        addArg {
            name = "response"
            type = returnDefType.builder()
        }
        setBody {
            add(assignment {
                left = instanceVariable(responseFieldName())
                right = variable("response")
            })
        }
    }

    //reset
    fun getResetAssignments(): List<AssignmentBuilder> {
        val assigns = mutableListOf(
            assignment {
                left = instanceVariable(callsNumberVariableName())
                right = const("0")
            }
        )
        argsApiType()?.let {
            assigns.add(
                assignment {
                    left = instanceVariable(callsVariableName())
                    right = emptyMutableList(it.builder())
                }
            )
        }
        if (supportResponse()) {
            assigns.add(
                assignment {
                    left = instanceVariable(responseFieldName())
                    right = returnDefType.emptyValueBuilder()
                }
            )
        }

        return assigns
    }

    // language specific
    fun setupAssignment(moduleName: String): AssignmentBuilder {
        return assignment {
            val apiMethodName = "$moduleName.Api.${def.getName()}"

            left = variable(apiMethodName)
            right = functionCall {
                name = "CreateMock"
                addArg {
                    variable(apiMethodName)
                }
                addArg {
                    lambda {
                        def.getArgs().forEach {
                            addArg {
                                name = it.getName()
                                type = apiTypeFactory.create(it.getType()).builder()
                            }
                        }
                        val mockMethodCall = methodCall {
                            target = variable("mock")
                            methodName = def.getName()
                            def.getArgs().forEach { arg ->
                                addArg {
                                    variable(arg.getName())
                                }
                            }
                        }
                        body = if (hasVoidReturnType()) {
                            mockMethodCall
                        } else {
                            returnExpression(mockMethodCall)
                        }
                    }
                }
            }
        }
    }

    fun supportResponse() = !hasVoidReturnType() && !hasExternalReturnType()

    //private
    private fun callsNumberVariableName(): String {
        return def.getName() + "CallsNumber"
    }

    private fun callsVariableName(): String {
        return def.getName() + "Calls"
    }

    private fun argsApiType(): ApiType? {
        return when (def.getArgs().size) {
            0 -> null
            1 -> apiTypeFactory.create(def.getArgs()[0].getType())
            else -> {
                return apiTypeFactory.create(
                    TypeDefinition.create(
                        name = methodsArgsTypeName(interfDef, def),
                        wrappers = emptyList()
                    )
                )
            }
        }
    }

    private fun argsDefType(): DefType<*>? {
        return argsApiType()?.let { defTypeFactory.create(it) }
    }

    private fun supportedArgsExpectedType(): ExpectedType<*>? {
        val type = argsApiType()?.let { expectedTypeFactory.create(it) }
        if (type is ExternalExpectedType || type is OptionalExpectedType || type is ListExpectedType) {
            return null
        }
        if (type is BaseExpectedType && type.api.name == com.github.bratek20.hla.definitions.api.BaseType.ANY) {
            return null
        }
        return type
    }

    private fun responseFieldName(): String {
        return def.getName() + "Response"
    }

    private fun hasVoidReturnType(): Boolean {
        return BaseApiType.isVoid(apiTypeFactory.create(def.getReturnType()))
    }

    private fun hasExternalReturnType(): Boolean {
        return returnApiType is ExternalApiType
    }

    private fun defaultBuilderCall(type: ApiType): ExpressionBuilder {
        return defTypeFactory.create(type).modernBuild(instanceVariable(def.getName() + "Response"))
    }
}

class MockInterfaceLogic(
    private val def: InterfaceDefinition,
    private val moduleName: String,
    private val apiTypeFactory: ApiTypeFactory,
    private val defTypeFactory: DefTypeFactory,
    private val expectedTypeFactory: ExpectedTypeFactory,
    private val languageName: ModuleLanguage,
    private val modules: ModuleGroupQueries
) {
    fun mockClass(): ClassBuilderOps = {
        name = def.getName() + "Mock"
        implements = def.getName()

        getMethodsLogic().forEach { method ->
            addField(method.callsNumberField())
            method.callsField()?.let {
                addField(it)
            }

            addMethod(method.mockedMethod())
            addMethod(method.callsNumberAssertion())
            method.callsAssertion()?.let {
                addMethod(it)
            }

            if (method.supportResponse()) {
                addField(method.responseField())
                addMethod(method.setResponse())
            }
        }

        addMethod(resetMethod())
    }
    private fun getMethodsLogic(): List<MockMethodLogic> {
        return def.getMethods().map { methodDef ->
            createMethodLogic(methodDef)
        }
    }

    private fun createMethodLogic(methodDef: MethodDefinition): MockMethodLogic {
        return MockMethodLogic(def, methodDef, apiTypeFactory, defTypeFactory, expectedTypeFactory, languageName, modules)
    }

    private fun resetMethod(): MethodBuilderOps = {
        name = "reset"
        setBody {
            addMany(getMethodsLogic().flatMap { it.getResetAssignments() })
        }
    }

    fun createMock(): FunctionBuilderOps = {
        name = "create${def.getName()}Mock"
        returnType = typeName(def.getName() + "Mock")
        setBody {
            add(returnStatement {
                constructorCall {
                    className = def.getName() + "Mock"
                }
            })
        }
    }

    fun setup(): FunctionBuilderOps = {
        name = "setup${def.getName()}"
        returnType = typeName(def.getName() + "Mock")
        setBody {
            add(assignment {
                left = variableDeclaration {
                    name = "mock"
                }
                right = functionCall {
                    name = "$moduleName.Mocks.create${def.getName()}Mock"
                }
            })

            getMethodsLogic().forEach { method ->
                add(method.setupAssignment(moduleName))
            }

            add(returnStatement {
                variable("mock")
            })
        }
    }
}
class MocksGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun extraKotlinImports(): List<String> {
        val moduleName = module.getName().value.lowercase()
        val modulePackage = profileToRootPackage(moduleGroup.getProfile())

        return listOf(
            "$modulePackage.$moduleName.api.*",
            "org.assertj.core.api.Assertions.assertThat"
        )
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return getMockedInterfaces().isNotEmpty() && language.name() != ModuleLanguage.C_SHARP
    }

    private fun getMockedInterfaces(): List<InterfaceDefinition> {
        return modules.getMockedInterfaces(module)
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val mockInterfacesLogic = getMockedInterfaces().map { MockInterfaceLogic(
            it,
            moduleName,
            apiTypeFactory,
            DefTypeFactory(language.buildersFixture()),
            ExpectedTypeFactory(c),
            language.name(),
            c.domain.queries
        ) }

        mockInterfacesLogic.forEach { logic ->
            addClass(logic.mockClass())
        }

        if(language.name() == ModuleLanguage.TYPE_SCRIPT) {
            add(typeScriptNamespace {
                name = "$moduleName.Mocks"
                mockInterfacesLogic.forEach { logic ->
                    addFunction(logic.createMock())
                    addFunction(logic.setup())
                }
            })
        }
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}