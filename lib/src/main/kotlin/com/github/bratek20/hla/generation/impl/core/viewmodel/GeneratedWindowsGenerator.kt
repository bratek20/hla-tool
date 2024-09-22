package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.ViewModelWindowDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory

class GeneratedWindowLogic(
    private val def: ViewModelWindowDefinition,
    private val apiTypeFactory: ApiTypeFactory
) {
    fun getState(): ClassBuilderOps = {
        name = def.getName() + "State"

        def.getState()!!.getFields().forEach { field ->
            addField {
                type = apiTypeFactory.create(field.getType()).builder()
                name = field.getName()
                getter = true
                fromConstructor = true
            }
        }
    }

    fun getWindowClass(): ClassBuilderOps = {
        name = def.getName()
        partial = true
        extends {
            className = "Window"
            generic = typeName("SomeWindowState")
        }

        def.getFields().forEach { field ->
            addField {
                type = typeName(field.getType().getName())
                name = field.getName()
                getter = true
                setter = true
            }
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
        viewModelWindows()?.forEach { def ->
            val logic = GeneratedWindowLogic(def, apiTypeFactory)
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