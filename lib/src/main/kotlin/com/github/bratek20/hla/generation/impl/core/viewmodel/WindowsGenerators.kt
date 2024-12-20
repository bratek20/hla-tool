package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.definitions.api.ViewModelWindowDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

class GeneratedWindowLogic(
    private val moduleName: ModuleName,
    private val def: ViewModelWindowDefinition,
    private val apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi,
    type: WorldType
): ViewModelLogic(typesWorldApi, type) {
    fun getModuleName(): String {
        return moduleName.value
    }

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

    fun getFields(): List<ViewModelField> {
        val type = typesWorldApi.getTypeByName(WorldTypeName(def.getName()))
        val classType = typesWorldApi.getClassType(type)
        return classType.getFields().map { field ->
            ViewModelField(
                typeName = field.getType().getName().value,
                name = field.getName(),
            )
        }
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

        getFields().forEach { field ->
            addField {
                type = typeName(field.typeName)
                name = field.name
                getter = true
                setter = true
            }
        }
    }
}

abstract class BaseViewModelPatternGenerator: PatternGenerator() {
    protected lateinit var logic: ViewModelSharedLogic

    override fun init(c: ModuleGenerationContext, velocityPath: String, typesWorldApi: TypesWorldApi) {
        super.init(c, velocityPath, typesWorldApi)
        logic = ViewModelSharedLogic(module, apiTypeFactory, typesWorldApi)
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
            "B20.ViewModel.UiElements.Api",
            "B20.ViewModel.Windows.Api",
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