// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.definitions.fixtures

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*

import com.github.bratek20.hla.definitions.api.*

data class KeyDefinitionDef(
    var name: String = "someValue",
    var type: (TypeDefinitionDef.() -> Unit) = {},
)
fun keyDefinition(init: KeyDefinitionDef.() -> Unit = {}): KeyDefinition {
    val def = KeyDefinitionDef().apply(init)
    return KeyDefinition.create(
        name = def.name,
        type = typeDefinition(def.type),
    )
}

data class EnumDefinitionDef(
    var name: String = "someValue",
    var values: List<String> = emptyList(),
)
fun enumDefinition(init: EnumDefinitionDef.() -> Unit = {}): EnumDefinition {
    val def = EnumDefinitionDef().apply(init)
    return EnumDefinition.create(
        name = def.name,
        values = def.values,
    )
}

data class ImplSubmoduleDefinitionDef(
    var dataClasses: List<(ComplexStructureDefinitionDef.() -> Unit)> = emptyList(),
    var dataKeys: List<(KeyDefinitionDef.() -> Unit)> = emptyList(),
)
fun implSubmoduleDefinition(init: ImplSubmoduleDefinitionDef.() -> Unit = {}): ImplSubmoduleDefinition {
    val def = ImplSubmoduleDefinitionDef().apply(init)
    return ImplSubmoduleDefinition.create(
        dataClasses = def.dataClasses.map { it -> complexStructureDefinition(it) },
        dataKeys = def.dataKeys.map { it -> keyDefinition(it) },
    )
}

data class HttpDefinitionDef(
    var exposedInterfaces: List<String> = emptyList(),
    var serverName: String? = null,
    var baseUrl: String? = null,
    var auth: String? = null,
    var urlPathPrefix: String? = null,
)
fun httpDefinition(init: HttpDefinitionDef.() -> Unit = {}): HttpDefinition {
    val def = HttpDefinitionDef().apply(init)
    return HttpDefinition.create(
        exposedInterfaces = def.exposedInterfaces,
        serverName = def.serverName,
        baseUrl = def.baseUrl,
        auth = def.auth,
        urlPathPrefix = def.urlPathPrefix,
    )
}

data class ExposedInterfaceDef(
    var name: String = "someValue",
    var attributes: List<(AttributeDef.() -> Unit)> = emptyList(),
)
fun exposedInterface(init: ExposedInterfaceDef.() -> Unit = {}): ExposedInterface {
    val def = ExposedInterfaceDef().apply(init)
    return ExposedInterface.create(
        name = def.name,
        attributes = def.attributes.map { it -> attribute(it) },
    )
}

data class ErrorCodeMappingDef(
    var exceptionName: String = "someValue",
    var code: String = "someValue",
)
fun errorCodeMapping(init: ErrorCodeMappingDef.() -> Unit = {}): ErrorCodeMapping {
    val def = ErrorCodeMappingDef().apply(init)
    return ErrorCodeMapping.create(
        exceptionName = def.exceptionName,
        code = def.code,
    )
}

data class PlayFabHandlersDefinitionDef(
    var exposedInterfaces: List<(ExposedInterfaceDef.() -> Unit)> = emptyList(),
    var errorCodesMapping: List<(ErrorCodeMappingDef.() -> Unit)> = emptyList(),
)
fun playFabHandlersDefinition(init: PlayFabHandlersDefinitionDef.() -> Unit = {}): PlayFabHandlersDefinition {
    val def = PlayFabHandlersDefinitionDef().apply(init)
    return PlayFabHandlersDefinition.create(
        exposedInterfaces = def.exposedInterfaces.map { it -> exposedInterface(it) },
        errorCodesMapping = def.errorCodesMapping.map { it -> errorCodeMapping(it) },
    )
}

data class WebSubmoduleDefinitionDef(
    var http: (HttpDefinitionDef.() -> Unit)? = null,
    var playFabHandlers: (PlayFabHandlersDefinitionDef.() -> Unit)? = null,
)
fun webSubmoduleDefinition(init: WebSubmoduleDefinitionDef.() -> Unit = {}): WebSubmoduleDefinition {
    val def = WebSubmoduleDefinitionDef().apply(init)
    return WebSubmoduleDefinition.create(
        http = def.http?.let { it -> httpDefinition(it) },
        playFabHandlers = def.playFabHandlers?.let { it -> playFabHandlersDefinition(it) },
    )
}

data class ViewModelMappedFieldDef(
    var name: String = "someValue",
    var overriddenViewModelType: String? = null,
)
fun viewModelMappedField(init: ViewModelMappedFieldDef.() -> Unit = {}): ViewModelMappedField {
    val def = ViewModelMappedFieldDef().apply(init)
    return ViewModelMappedField.create(
        name = def.name,
        overriddenViewModelType = def.overriddenViewModelType,
    )
}

data class ElementModelDefinitionDef(
    var name: String = "someValue",
    var mappedFields: List<(ViewModelMappedFieldDef.() -> Unit)> = emptyList(),
)
fun elementModelDefinition(init: ElementModelDefinitionDef.() -> Unit = {}): ElementModelDefinition {
    val def = ElementModelDefinitionDef().apply(init)
    return ElementModelDefinition.create(
        name = def.name,
        mappedFields = def.mappedFields.map { it -> viewModelMappedField(it) },
    )
}

data class UiElementDefinitionDef(
    var name: String = "someValue",
    var attributes: List<(AttributeDef.() -> Unit)> = emptyList(),
    var model: (ElementModelDefinitionDef.() -> Unit)? = null,
    var fields: List<(FieldDefinitionDef.() -> Unit)> = emptyList(),
)
fun uiElementDefinition(init: UiElementDefinitionDef.() -> Unit = {}): UiElementDefinition {
    val def = UiElementDefinitionDef().apply(init)
    return UiElementDefinition.create(
        name = def.name,
        attributes = def.attributes.map { it -> attribute(it) },
        model = def.model?.let { it -> elementModelDefinition(it) },
        fields = def.fields.map { it -> fieldDefinition(it) },
    )
}

data class UiContainerDefinitionDef(
    var name: String = "someValue",
    var state: (ComplexStructureDefinitionDef.() -> Unit)? = null,
    var fields: List<(FieldDefinitionDef.() -> Unit)> = emptyList(),
)
fun uiContainerDefinition(init: UiContainerDefinitionDef.() -> Unit = {}): UiContainerDefinition {
    val def = UiContainerDefinitionDef().apply(init)
    return UiContainerDefinition.create(
        name = def.name,
        state = def.state?.let { it -> complexStructureDefinition(it) },
        fields = def.fields.map { it -> fieldDefinition(it) },
    )
}

data class ViewModelSubmoduleDefinitionDef(
    var elements: List<(UiElementDefinitionDef.() -> Unit)> = emptyList(),
    var windows: List<(UiContainerDefinitionDef.() -> Unit)> = emptyList(),
    var popups: List<(UiContainerDefinitionDef.() -> Unit)> = emptyList(),
)
fun viewModelSubmoduleDefinition(init: ViewModelSubmoduleDefinitionDef.() -> Unit = {}): ViewModelSubmoduleDefinition {
    val def = ViewModelSubmoduleDefinitionDef().apply(init)
    return ViewModelSubmoduleDefinition.create(
        elements = def.elements.map { it -> uiElementDefinition(it) },
        windows = def.windows.map { it -> uiContainerDefinition(it) },
        popups = def.popups.map { it -> uiContainerDefinition(it) },
    )
}

data class ExternalTypePackageMappingDef(
    var name: String = "someValue",
    var packageName: String = "someValue",
)
fun externalTypePackageMapping(init: ExternalTypePackageMappingDef.() -> Unit = {}): ExternalTypePackageMapping {
    val def = ExternalTypePackageMappingDef().apply(init)
    return ExternalTypePackageMapping.create(
        name = def.name,
        packageName = def.packageName,
    )
}

data class KotlinConfigDef(
    var externalTypePackages: List<(ExternalTypePackageMappingDef.() -> Unit)> = emptyList(),
    var records: List<String> = emptyList(),
)
fun kotlinConfig(init: KotlinConfigDef.() -> Unit = {}): KotlinConfig {
    val def = KotlinConfigDef().apply(init)
    return KotlinConfig.create(
        externalTypePackages = def.externalTypePackages.map { it -> externalTypePackageMapping(it) },
        records = def.records,
    )
}

data class ModuleDefinitionDef(
    var name: String = "someValue",
    var simpleCustomTypes: List<(SimpleStructureDefinitionDef.() -> Unit)> = emptyList(),
    var complexCustomTypes: List<(ComplexStructureDefinitionDef.() -> Unit)> = emptyList(),
    var simpleValueObjects: List<(SimpleStructureDefinitionDef.() -> Unit)> = emptyList(),
    var complexValueObjects: List<(ComplexStructureDefinitionDef.() -> Unit)> = emptyList(),
    var dataClasses: List<(ComplexStructureDefinitionDef.() -> Unit)> = emptyList(),
    var exceptions: List<(ExceptionDefinitionDef.() -> Unit)> = emptyList(),
    var events: List<(ComplexStructureDefinitionDef.() -> Unit)> = emptyList(),
    var interfaces: List<(InterfaceDefinitionDef.() -> Unit)> = emptyList(),
    var propertyKeys: List<(KeyDefinitionDef.() -> Unit)> = emptyList(),
    var dataKeys: List<(KeyDefinitionDef.() -> Unit)> = emptyList(),
    var enums: List<(EnumDefinitionDef.() -> Unit)> = emptyList(),
    var externalTypes: List<String> = emptyList(),
    var implSubmodule: (ImplSubmoduleDefinitionDef.() -> Unit)? = null,
    var webSubmodule: (WebSubmoduleDefinitionDef.() -> Unit)? = null,
    var viewModelSubmodule: (ViewModelSubmoduleDefinitionDef.() -> Unit)? = null,
    var kotlinConfig: (KotlinConfigDef.() -> Unit)? = null,
)
fun moduleDefinition(init: ModuleDefinitionDef.() -> Unit = {}): ModuleDefinition {
    val def = ModuleDefinitionDef().apply(init)
    return ModuleDefinition.create(
        name = ModuleName(def.name),
        simpleCustomTypes = def.simpleCustomTypes.map { it -> simpleStructureDefinition(it) },
        complexCustomTypes = def.complexCustomTypes.map { it -> complexStructureDefinition(it) },
        simpleValueObjects = def.simpleValueObjects.map { it -> simpleStructureDefinition(it) },
        complexValueObjects = def.complexValueObjects.map { it -> complexStructureDefinition(it) },
        dataClasses = def.dataClasses.map { it -> complexStructureDefinition(it) },
        exceptions = def.exceptions.map { it -> exceptionDefinition(it) },
        events = def.events.map { it -> complexStructureDefinition(it) },
        interfaces = def.interfaces.map { it -> interfaceDefinition(it) },
        propertyKeys = def.propertyKeys.map { it -> keyDefinition(it) },
        dataKeys = def.dataKeys.map { it -> keyDefinition(it) },
        enums = def.enums.map { it -> enumDefinition(it) },
        externalTypes = def.externalTypes,
        implSubmodule = def.implSubmodule?.let { it -> implSubmoduleDefinition(it) },
        webSubmodule = def.webSubmodule?.let { it -> webSubmoduleDefinition(it) },
        viewModelSubmodule = def.viewModelSubmodule?.let { it -> viewModelSubmoduleDefinition(it) },
        kotlinConfig = def.kotlinConfig?.let { it -> kotlinConfig(it) },
    )
}

data class TypeDefinitionDef(
    var name: String = "someValue",
    var wrappers: List<String> = emptyList(),
)
fun typeDefinition(init: TypeDefinitionDef.() -> Unit = {}): TypeDefinition {
    val def = TypeDefinitionDef().apply(init)
    return TypeDefinition.create(
        name = def.name,
        wrappers = def.wrappers.map { it -> TypeWrapper.valueOf(it) },
    )
}

data class FieldDefinitionDef(
    var name: String = "someValue",
    var type: (TypeDefinitionDef.() -> Unit) = {},
    var attributes: List<(AttributeDef.() -> Unit)> = emptyList(),
    var defaultValue: String? = null,
)
fun fieldDefinition(init: FieldDefinitionDef.() -> Unit = {}): FieldDefinition {
    val def = FieldDefinitionDef().apply(init)
    return FieldDefinition.create(
        name = def.name,
        type = typeDefinition(def.type),
        attributes = def.attributes.map { it -> attribute(it) },
        defaultValue = def.defaultValue,
    )
}

data class AttributeDef(
    var name: String = "someValue",
    var value: String = "someValue",
)
fun attribute(init: AttributeDef.() -> Unit = {}): Attribute {
    val def = AttributeDef().apply(init)
    return Attribute.create(
        name = def.name,
        value = def.value,
    )
}

data class SimpleStructureDefinitionDef(
    var name: String = "someValue",
    var typeName: String = "someValue",
    var attributes: List<(AttributeDef.() -> Unit)> = emptyList(),
)
fun simpleStructureDefinition(init: SimpleStructureDefinitionDef.() -> Unit = {}): SimpleStructureDefinition {
    val def = SimpleStructureDefinitionDef().apply(init)
    return SimpleStructureDefinition.create(
        name = def.name,
        typeName = def.typeName,
        attributes = def.attributes.map { it -> attribute(it) },
    )
}

data class ComplexStructureDefinitionDef(
    var name: String = "someValue",
    var fields: List<(FieldDefinitionDef.() -> Unit)> = emptyList(),
)
fun complexStructureDefinition(init: ComplexStructureDefinitionDef.() -> Unit = {}): ComplexStructureDefinition {
    val def = ComplexStructureDefinitionDef().apply(init)
    return ComplexStructureDefinition.create(
        name = def.name,
        fields = def.fields.map { it -> fieldDefinition(it) },
    )
}

data class InterfaceDefinitionDef(
    var name: String = "someValue",
    var methods: List<(MethodDefinitionDef.() -> Unit)> = emptyList(),
)
fun interfaceDefinition(init: InterfaceDefinitionDef.() -> Unit = {}): InterfaceDefinition {
    val def = InterfaceDefinitionDef().apply(init)
    return InterfaceDefinition.create(
        name = def.name,
        methods = def.methods.map { it -> methodDefinition(it) },
    )
}

data class ArgumentDefinitionDef(
    var name: String = "someValue",
    var type: (TypeDefinitionDef.() -> Unit) = {},
)
fun argumentDefinition(init: ArgumentDefinitionDef.() -> Unit = {}): ArgumentDefinition {
    val def = ArgumentDefinitionDef().apply(init)
    return ArgumentDefinition.create(
        name = def.name,
        type = typeDefinition(def.type),
    )
}

data class ExceptionDefinitionDef(
    var name: String = "someValue",
)
fun exceptionDefinition(init: ExceptionDefinitionDef.() -> Unit = {}): ExceptionDefinition {
    val def = ExceptionDefinitionDef().apply(init)
    return ExceptionDefinition.create(
        name = def.name,
    )
}

data class MethodDefinitionDef(
    var name: String = "someValue",
    var returnType: (TypeDefinitionDef.() -> Unit) = {},
    var args: List<(ArgumentDefinitionDef.() -> Unit)> = emptyList(),
    var throws: List<(ExceptionDefinitionDef.() -> Unit)> = emptyList(),
)
fun methodDefinition(init: MethodDefinitionDef.() -> Unit = {}): MethodDefinition {
    val def = MethodDefinitionDef().apply(init)
    return MethodDefinition.create(
        name = def.name,
        returnType = typeDefinition(def.returnType),
        args = def.args.map { it -> argumentDefinition(it) },
        throws = def.throws.map { it -> exceptionDefinition(it) },
    )
}