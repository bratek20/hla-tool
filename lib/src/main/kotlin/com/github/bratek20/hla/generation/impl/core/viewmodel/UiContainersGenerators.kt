package com.github.bratek20.hla.generation.impl.core.viewmodel

import com.github.bratek20.codebuilder.builders.ClassBuilderOps
import com.github.bratek20.codebuilder.builders.TopLevelCodeBuilderOps
import com.github.bratek20.codebuilder.types.typeName
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.definitions.api.UiContainerDefinition
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.impl.core.GeneratorMode
import com.github.bratek20.hla.generation.impl.core.ModuleGenerationContext
import com.github.bratek20.hla.generation.impl.core.PatternGenerator
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

abstract class GeneratedUiContainerLogic(
    private val moduleName: ModuleName,
    private val def: UiContainerDefinition,
    private val apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi,
): ViewModelLogic(typesWorldApi, typesWorldApi.getTypeByName(WorldTypeName(def.getName()))) {
    protected abstract fun getContainerName(): String

    fun getModuleName(): String {
        return moduleName.value
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
            className = getContainerName()
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

class GeneratedWindowLogic(
    moduleName: ModuleName,
    def: UiContainerDefinition,
    apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi
): GeneratedUiContainerLogic(
    moduleName,
    def,
    apiTypeFactory,
    typesWorldApi
) {
    override fun getContainerName(): String {
        return "Window"
    }
}

class GeneratedPopupLogic(
    moduleName: ModuleName,
    def: UiContainerDefinition,
    apiTypeFactory: ApiTypeFactoryLogic,
    typesWorldApi: TypesWorldApi
): GeneratedUiContainerLogic(
    moduleName,
    def,
    apiTypeFactory,
    typesWorldApi
) {
    override fun getContainerName(): String {
        return "Popup"
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
    protected abstract fun getDefs(): List<UiContainerDefinition>

    override fun shouldGenerate(): Boolean {
        return getDefs().isNotEmpty()
    }

    override fun extraCSharpUsings(): List<String> {
        return listOf(
            "B20.ViewModel.UiElements.Api",
            "B20.ViewModel.Windows.Api",
            "B20.ViewModel.Popups.Api",
        )
    }
}

abstract class GeneratedUiContainersGenerator: BaseWindowsGenerator() {
    protected abstract fun getLogicClasses(): List<GeneratedUiContainerLogic>

    override fun getOperations(): TopLevelCodeBuilderOps = {
        getLogicClasses().forEach { logic ->
            addClass(logic.getState())
            addClass(logic.getWindowClass())
        }
    }
}

class GeneratedWindowsGenerator: GeneratedUiContainersGenerator() {
    override fun getDefs(): List<UiContainerDefinition> {
        return logic.windowsDef()
    }

    override fun getLogicClasses(): List<GeneratedUiContainerLogic> {
        return logic.windowsLogic()
    }

    override fun patternName(): PatternName {
        return PatternName.GeneratedWindows
    }
}

class GeneratedPopupsGenerator: GeneratedUiContainersGenerator() {
    override fun getDefs(): List<UiContainerDefinition> {
        return logic.popupsDef()
    }

    override fun getLogicClasses(): List<GeneratedUiContainerLogic> {
        return logic.popupsLogic()
    }

    override fun patternName(): PatternName {
        return PatternName.GeneratedPopups
    }
}

abstract class UiContainersLogicGenerator: BaseWindowsGenerator() {

    override fun mode(): GeneratorMode {
        return GeneratorMode.ONLY_START
    }

    override fun getOperations(): TopLevelCodeBuilderOps = {
        getDefs().forEach { def ->
            addClass {
                name = def.getName()
                partial = true
            }
        }
    }
}

class WindowsLogicGenerator: UiContainersLogicGenerator() {
    override fun patternName(): PatternName {
        return PatternName.WindowsLogic
    }

    override fun getDefs(): List<UiContainerDefinition> {
        return logic.windowsDef()
    }
}

class PopupsLogicGenerator: UiContainersLogicGenerator() {
    override fun patternName(): PatternName {
        return PatternName.PopupsLogic
    }

    override fun getDefs(): List<UiContainerDefinition> {
        return logic.popupsDef()
    }
}