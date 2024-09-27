package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.definitions.api.ViewModelWindowDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.core.api.ListApiType

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
            addGeneric {
                typeName("SomeWindowState")
            }
        }

        def.getFields().forEach { field ->
            addField {
                type = typeName(getFinalFieldTypeName(field))
                name = field.getName()
                getter = true
                setter = true
            }
        }
    }

    private fun getFinalFieldTypeName(field: FieldDefinition): String {
        if (field.getType().getWrappers().contains(TypeWrapper.LIST)) {
            return field.getType().getName() + "Group"
        }
        return field.getType().getName()
    }

    fun getElementTypesWrappedInList(): List<String> {
        return def.getFields()
            .filter { it.getType().getWrappers().contains(TypeWrapper.LIST) }
            .map { it.getType().getName() }
    }
}

abstract class BaseViewModelPatternGenerator: PatternGenerator() {
    protected fun viewModelWindows() = module.getViewModelSubmodule()?.getWindows()

    protected fun viewModelWindowsLogic(): List<GeneratedWindowLogic> {
        return viewModelWindows()?.map { GeneratedWindowLogic(it, apiTypeFactory) } ?: emptyList()
    }
}

abstract class BaseWindowsGenerator: BaseViewModelPatternGenerator() {
    override fun supportsCodeBuilder(): Boolean {
        return true
    }

    override fun shouldGenerate(): Boolean {
        return viewModelWindows()?.isNotEmpty() ?: false
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.Frontend.Windows.Api",
            "B20.Frontend.UiElements"
        )
    }
}

class GeneratedWindowsGenerator: BaseWindowsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.GeneratedWindows
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        viewModelWindowsLogic().forEach { logic ->
            addClass(logic.getState())
            addClass(logic.getWindowClass())
        }
    }
}

class WindowsLogicGenerator: BaseWindowsGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WindowsLogic
    }

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        viewModelWindows()?.forEach { def ->
            addClass {
                name = def.getName()
                partial = true
            }
        }
    }
}