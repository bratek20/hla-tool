package com.github.bratek20.hla.generation.impl.core.fixtures

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class MocksGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Mocks
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        if (moduleName != "OtherModule") {
            return false
        }
        return module.getInterfaces().isNotEmpty() && language.name() == ModuleLanguage.TYPE_SCRIPT
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addClass {
            name = "OtherInterfaceMock"
            implements = "OtherInterface"

            addMethod {
                name = "otherMethod"
                returnType = baseType(BaseType.VOID)
            }
        }
    }

    override fun doNotGenerateTypeScriptNamespace(): Boolean {
        return true
    }
}