package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.core.BaseType
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.SubmoduleGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebClientGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebCommonGenerator
import com.github.bratek20.hla.generation.impl.core.web.http.WebServerGenerator


class MyPattern: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.Primitives
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun getOperations(): TopLevelCodeBuilderOps {
        return {

            module.getInterfaces().forEach {
                addFunction {
                    name = it.getName()
                    addArg {
                        name = it,
                        type = baseType(BaseType.STRING)
                    }
                }
            }
        }
    }
}

class WebGenerator: SubmoduleGenerator() {
    override fun submoduleName(): SubmoduleName {
        return SubmoduleName.Web
    }

    override fun velocityDirPath(): String {
        return "web"
    }

    override fun shouldGenerateSubmodule(): Boolean {
        return c.module.getWebSubmodule() != null
    }

    override fun getPatternGenerators(): List<PatternGenerator> {
        return listOf(
            WebCommonGenerator(),
            WebClientGenerator(),
            WebServerGenerator(),
            PlayFabHandlersGenerator(),
            WebServerContextGenerator(),
            WebClientContextGenerator(),
            MyPattern()
        )
    }
}