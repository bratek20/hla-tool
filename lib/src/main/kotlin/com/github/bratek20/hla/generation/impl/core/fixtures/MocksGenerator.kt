package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.builders.constructorCall
import com.github.bratek20.codebuilder.builders.returnStatement
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.languages.typescript.TypeScriptNamespaceBuilder
import com.github.bratek20.codebuilder.languages.typescript.typeScriptNamespace
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.InterfaceDefinition
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class MockInterfaceLogic(
    private val def: InterfaceDefinition
) {
    fun getClass(): ClassBuilderOps = {
        name = def.getName() + "Mock"
        implements = def.getName()

        def.getMethods().forEach { method ->
            addMethod {
                name = method.getName()
                returnType = baseType(BaseType.VOID)
            }
        }
    }
}
class MocksGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return module.getInterfaces().isNotEmpty() && language.name() == ModuleLanguage.TYPE_SCRIPT
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        val mockInterfacesLogic = module.getInterfaces().map { MockInterfaceLogic(it) }

        mockInterfacesLogic.forEach { logic ->
            addClass(logic.getClass())
        }

        add(typeScriptNamespace {
            name = "OtherModule.Mocks"
            addFunction {
                name = "createOtherInterfaceMock"
                returnType = typeName("OtherInterfaceMock")
                setBody {
                    add(returnStatement {
                       constructorCall {
                            className = "OtherInterfaceMock"
                       }
                    })
                }
            }
        })
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}