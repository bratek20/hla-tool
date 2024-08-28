package com.github.bratek20.hla.generation.impl.core.web

import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.ops.variable
import com.github.bratek20.hla.facade.api.ModuleLanguage
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class PlayFabHandlersGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PlayFabHandlers
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return c.language.name() == ModuleLanguage.TYPE_SCRIPT && c.module.getWebSubmodule()?.getPlayFabHandlers() != null
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        addFunctionCall {
            name = "Handlers.Api.RegisterModuleHandlers"
            addArg(variable("DependencyName.SomeModule"))
        }
        addEmptyLines(39)
    }
}