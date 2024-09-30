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

class GeneratedWindowLogic(
    private val def: ViewModelWindowDefinition,
    private val apiTypeFactory: ApiTypeFactory
) {
    fun getClassName(): String {
        return def.getName()
    }

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

    fun getFields(mapper: ModelToViewModelTypeMapper): List<ViewModelField> {
        return ViewModelField.fromDefs(def.getFields(), mapper)
    }

    fun getWindowClass(): ClassBuilderOps = {
        name = def.getName()
        partial = true
        extends {
            className = "Window"
            addGeneric {
                typeName(def.getName() + "State")
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

    fun getElementTypesWrappedIn(type: TypeWrapper): List<String> {
        return def.getFields()
            .filter { it.getType().getWrappers().contains(type) }
            .map { it.getType().getName() }
    }
}

abstract class BaseViewModelPatternGenerator: PatternGenerator() {
    protected val logic by lazy {
        ViewModelSharedLogic(module.getViewModelSubmodule(), apiTypeFactory)
    }

    override fun supportsCodeBuilder(): Boolean {
        return true
    }
}

abstract class BaseWindowsGenerator: BaseViewModelPatternGenerator() {

    override fun shouldGenerate(): Boolean {
        return logic.windowsDef().isNotEmpty()
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
        logic.windowsLogic().forEach { logic ->
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
        logic.windowsDef().forEach { def ->
            addClass {
                name = def.getName()
                partial = true
            }
        }
    }
}