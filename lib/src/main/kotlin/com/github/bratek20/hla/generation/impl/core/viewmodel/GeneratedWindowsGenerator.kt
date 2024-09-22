package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator

class GeneratedWindowLogic() {
    fun getState(): ClassBuilderOps = {
        name = "SomeWindowState"

        addField {
            type = typeName("SomeId")
            name = "someId"
            getter = true
            fromConstructor = true
        }
    }

    fun getWindowClass(): ClassBuilderOps = {
        name = "SomeWindow"
        partial = true
        extends {
            className = "Window"
            generic = typeName("SomeWindowState")
        }

        addField {
            type = typeName("SomeClassVm")
            name = "someClassVm"
            getter = true
            setter = true
        }
    }
}
class GeneratedWindowsGenerator: PatternGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedWindows
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    private fun viewModelWindows() = module.getViewModelSubmodule()?.getWindows()

    override fun shouldGenerate(): Boolean {
        return viewModelWindows()?.isNotEmpty() ?: false
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        viewModelWindows()?.forEach { window ->
            val logic = GeneratedWindowLogic()
            addClass(logic.getState())
            addClass(logic.getWindowClass())
        }
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Windows.Api",
            "B20.Frontend.UiElements"
        )
    }
}