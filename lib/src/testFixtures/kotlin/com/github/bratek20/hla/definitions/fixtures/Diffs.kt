// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.definitions.fixtures

import com.github.bratek20.hla.facade.api.*
import com.github.bratek20.hla.facade.fixtures.*

import com.github.bratek20.hla.definitions.api.*

fun diffBaseType(given: BaseType, expected: String, path: String = ""): String {
    if (given != BaseType.valueOf(expected)) { return "${path}value ${given.name} != ${expected}" }
    return ""
}

fun diffTypeWrapper(given: TypeWrapper, expected: String, path: String = ""): String {
    if (given != TypeWrapper.valueOf(expected)) { return "${path}value ${given.name} != ${expected}" }
    return ""
}

data class ExpectedKeyDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun diffKeyDefinition(given: KeyDefinition, expectedInit: ExpectedKeyDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedKeyDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedEnumDefinition(
    var name: String? = null,
    var values: List<String>? = null,
)
fun diffEnumDefinition(given: EnumDefinition, expectedInit: ExpectedEnumDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedEnumDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.values?.let {
        if (given.getValues().size != it.size) { result.add("${path}values size ${given.getValues().size} != ${it.size}"); return@let }
        given.getValues().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}values[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}

data class ExpectedImplSubmoduleDefinition(
    var dataClasses: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var dataKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
)
fun diffImplSubmoduleDefinition(given: ImplSubmoduleDefinition, expectedInit: ExpectedImplSubmoduleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedImplSubmoduleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.dataClasses?.let {
        if (given.getDataClasses().size != it.size) { result.add("${path}dataClasses size ${given.getDataClasses().size} != ${it.size}"); return@let }
        given.getDataClasses().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}dataClasses[${idx}].")) } }
    }

    expected.dataKeys?.let {
        if (given.getDataKeys().size != it.size) { result.add("${path}dataKeys size ${given.getDataKeys().size} != ${it.size}"); return@let }
        given.getDataKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}dataKeys[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedHttpDefinition(
    var exposedInterfaces: List<String>? = null,
    var serverNameEmpty: Boolean? = null,
    var serverName: String? = null,
    var baseUrlEmpty: Boolean? = null,
    var baseUrl: String? = null,
    var authEmpty: Boolean? = null,
    var auth: String? = null,
    var urlPathPrefixEmpty: Boolean? = null,
    var urlPathPrefix: String? = null,
)
fun diffHttpDefinition(given: HttpDefinition, expectedInit: ExpectedHttpDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedHttpDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.exposedInterfaces?.let {
        if (given.getExposedInterfaces().size != it.size) { result.add("${path}exposedInterfaces size ${given.getExposedInterfaces().size} != ${it.size}"); return@let }
        given.getExposedInterfaces().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}exposedInterfaces[${idx}] ${entry} != ${it[idx]}") } }
    }

    expected.serverNameEmpty?.let {
        if ((given.getServerName() == null) != it) { result.add("${path}serverName empty ${(given.getServerName() == null)} != ${it}") }
    }

    expected.serverName?.let {
        if (given.getServerName()!! != it) { result.add("${path}serverName ${given.getServerName()!!} != ${it}") }
    }

    expected.baseUrlEmpty?.let {
        if ((given.getBaseUrl() == null) != it) { result.add("${path}baseUrl empty ${(given.getBaseUrl() == null)} != ${it}") }
    }

    expected.baseUrl?.let {
        if (given.getBaseUrl()!! != it) { result.add("${path}baseUrl ${given.getBaseUrl()!!} != ${it}") }
    }

    expected.authEmpty?.let {
        if ((given.getAuth() == null) != it) { result.add("${path}auth empty ${(given.getAuth() == null)} != ${it}") }
    }

    expected.auth?.let {
        if (given.getAuth()!! != it) { result.add("${path}auth ${given.getAuth()!!} != ${it}") }
    }

    expected.urlPathPrefixEmpty?.let {
        if ((given.getUrlPathPrefix() == null) != it) { result.add("${path}urlPathPrefix empty ${(given.getUrlPathPrefix() == null)} != ${it}") }
    }

    expected.urlPathPrefix?.let {
        if (given.getUrlPathPrefix()!! != it) { result.add("${path}urlPathPrefix ${given.getUrlPathPrefix()!!} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedExposedInterface(
    var name: String? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
)
fun diffExposedInterface(given: ExposedInterface, expectedInit: ExpectedExposedInterface.() -> Unit, path: String = ""): String {
    val expected = ExpectedExposedInterface().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}"); return@let }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedErrorCodeMapping(
    var exceptionName: String? = null,
    var code: String? = null,
)
fun diffErrorCodeMapping(given: ErrorCodeMapping, expectedInit: ExpectedErrorCodeMapping.() -> Unit, path: String = ""): String {
    val expected = ExpectedErrorCodeMapping().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.exceptionName?.let {
        if (given.getExceptionName() != it) { result.add("${path}exceptionName ${given.getExceptionName()} != ${it}") }
    }

    expected.code?.let {
        if (given.getCode() != it) { result.add("${path}code ${given.getCode()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedPlayFabHandlersDefinition(
    var exposedInterfaces: List<(ExpectedExposedInterface.() -> Unit)>? = null,
    var errorCodesMapping: List<(ExpectedErrorCodeMapping.() -> Unit)>? = null,
)
fun diffPlayFabHandlersDefinition(given: PlayFabHandlersDefinition, expectedInit: ExpectedPlayFabHandlersDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedPlayFabHandlersDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.exposedInterfaces?.let {
        if (given.getExposedInterfaces().size != it.size) { result.add("${path}exposedInterfaces size ${given.getExposedInterfaces().size} != ${it.size}"); return@let }
        given.getExposedInterfaces().forEachIndexed { idx, entry -> if (diffExposedInterface(entry, it[idx]) != "") { result.add(diffExposedInterface(entry, it[idx], "${path}exposedInterfaces[${idx}].")) } }
    }

    expected.errorCodesMapping?.let {
        if (given.getErrorCodesMapping().size != it.size) { result.add("${path}errorCodesMapping size ${given.getErrorCodesMapping().size} != ${it.size}"); return@let }
        given.getErrorCodesMapping().forEachIndexed { idx, entry -> if (diffErrorCodeMapping(entry, it[idx]) != "") { result.add(diffErrorCodeMapping(entry, it[idx], "${path}errorCodesMapping[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedWebSubmoduleDefinition(
    var httpEmpty: Boolean? = null,
    var http: (ExpectedHttpDefinition.() -> Unit)? = null,
    var playFabHandlersEmpty: Boolean? = null,
    var playFabHandlers: (ExpectedPlayFabHandlersDefinition.() -> Unit)? = null,
)
fun diffWebSubmoduleDefinition(given: WebSubmoduleDefinition, expectedInit: ExpectedWebSubmoduleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedWebSubmoduleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.httpEmpty?.let {
        if ((given.getHttp() == null) != it) { result.add("${path}http empty ${(given.getHttp() == null)} != ${it}") }
    }

    expected.http?.let {
        if (diffHttpDefinition(given.getHttp()!!, it) != "") { result.add(diffHttpDefinition(given.getHttp()!!, it, "${path}http.")) }
    }

    expected.playFabHandlersEmpty?.let {
        if ((given.getPlayFabHandlers() == null) != it) { result.add("${path}playFabHandlers empty ${(given.getPlayFabHandlers() == null)} != ${it}") }
    }

    expected.playFabHandlers?.let {
        if (diffPlayFabHandlersDefinition(given.getPlayFabHandlers()!!, it) != "") { result.add(diffPlayFabHandlersDefinition(given.getPlayFabHandlers()!!, it, "${path}playFabHandlers.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedElementModelDefinition(
    var name: String? = null,
    var mappedFields: List<String>? = null,
)
fun diffElementModelDefinition(given: ElementModelDefinition, expectedInit: ExpectedElementModelDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedElementModelDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.mappedFields?.let {
        if (given.getMappedFields().size != it.size) { result.add("${path}mappedFields size ${given.getMappedFields().size} != ${it.size}"); return@let }
        given.getMappedFields().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}mappedFields[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}

data class ExpectedViewModelElementDefinition(
    var name: String? = null,
    var model: (ExpectedElementModelDefinition.() -> Unit)? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun diffViewModelElementDefinition(given: ViewModelElementDefinition, expectedInit: ExpectedViewModelElementDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedViewModelElementDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.model?.let {
        if (diffElementModelDefinition(given.getModel(), it) != "") { result.add(diffElementModelDefinition(given.getModel(), it, "${path}model.")) }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}"); return@let }
        given.getFields().forEachIndexed { idx, entry -> if (diffFieldDefinition(entry, it[idx]) != "") { result.add(diffFieldDefinition(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedViewModelWindowDefinition(
    var name: String? = null,
    var stateEmpty: Boolean? = null,
    var state: (ExpectedComplexStructureDefinition.() -> Unit)? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun diffViewModelWindowDefinition(given: ViewModelWindowDefinition, expectedInit: ExpectedViewModelWindowDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedViewModelWindowDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.stateEmpty?.let {
        if ((given.getState() == null) != it) { result.add("${path}state empty ${(given.getState() == null)} != ${it}") }
    }

    expected.state?.let {
        if (diffComplexStructureDefinition(given.getState()!!, it) != "") { result.add(diffComplexStructureDefinition(given.getState()!!, it, "${path}state.")) }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}"); return@let }
        given.getFields().forEachIndexed { idx, entry -> if (diffFieldDefinition(entry, it[idx]) != "") { result.add(diffFieldDefinition(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedViewModelSubmoduleDefinition(
    var elements: List<(ExpectedViewModelElementDefinition.() -> Unit)>? = null,
    var windows: List<(ExpectedViewModelWindowDefinition.() -> Unit)>? = null,
)
fun diffViewModelSubmoduleDefinition(given: ViewModelSubmoduleDefinition, expectedInit: ExpectedViewModelSubmoduleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedViewModelSubmoduleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.elements?.let {
        if (given.getElements().size != it.size) { result.add("${path}elements size ${given.getElements().size} != ${it.size}"); return@let }
        given.getElements().forEachIndexed { idx, entry -> if (diffViewModelElementDefinition(entry, it[idx]) != "") { result.add(diffViewModelElementDefinition(entry, it[idx], "${path}elements[${idx}].")) } }
    }

    expected.windows?.let {
        if (given.getWindows().size != it.size) { result.add("${path}windows size ${given.getWindows().size} != ${it.size}"); return@let }
        given.getWindows().forEachIndexed { idx, entry -> if (diffViewModelWindowDefinition(entry, it[idx]) != "") { result.add(diffViewModelWindowDefinition(entry, it[idx], "${path}windows[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedExternalTypePackageMapping(
    var name: String? = null,
    var packageName: String? = null,
)
fun diffExternalTypePackageMapping(given: ExternalTypePackageMapping, expectedInit: ExpectedExternalTypePackageMapping.() -> Unit, path: String = ""): String {
    val expected = ExpectedExternalTypePackageMapping().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.packageName?.let {
        if (given.getPackageName() != it) { result.add("${path}packageName ${given.getPackageName()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedKotlinConfig(
    var externalTypePackages: List<(ExpectedExternalTypePackageMapping.() -> Unit)>? = null,
    var records: List<String>? = null,
)
fun diffKotlinConfig(given: KotlinConfig, expectedInit: ExpectedKotlinConfig.() -> Unit, path: String = ""): String {
    val expected = ExpectedKotlinConfig().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.externalTypePackages?.let {
        if (given.getExternalTypePackages().size != it.size) { result.add("${path}externalTypePackages size ${given.getExternalTypePackages().size} != ${it.size}"); return@let }
        given.getExternalTypePackages().forEachIndexed { idx, entry -> if (diffExternalTypePackageMapping(entry, it[idx]) != "") { result.add(diffExternalTypePackageMapping(entry, it[idx], "${path}externalTypePackages[${idx}].")) } }
    }

    expected.records?.let {
        if (given.getRecords().size != it.size) { result.add("${path}records size ${given.getRecords().size} != ${it.size}"); return@let }
        given.getRecords().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}records[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}

data class ExpectedModuleDefinition(
    var name: String? = null,
    var simpleCustomTypes: List<(ExpectedSimpleStructureDefinition.() -> Unit)>? = null,
    var complexCustomTypes: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var simpleValueObjects: List<(ExpectedSimpleStructureDefinition.() -> Unit)>? = null,
    var complexValueObjects: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var dataClasses: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var interfaces: List<(ExpectedInterfaceDefinition.() -> Unit)>? = null,
    var propertyKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
    var dataKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
    var enums: List<(ExpectedEnumDefinition.() -> Unit)>? = null,
    var externalTypes: List<String>? = null,
    var implSubmoduleEmpty: Boolean? = null,
    var implSubmodule: (ExpectedImplSubmoduleDefinition.() -> Unit)? = null,
    var webSubmoduleEmpty: Boolean? = null,
    var webSubmodule: (ExpectedWebSubmoduleDefinition.() -> Unit)? = null,
    var viewModelSubmoduleEmpty: Boolean? = null,
    var viewModelSubmodule: (ExpectedViewModelSubmoduleDefinition.() -> Unit)? = null,
    var kotlinConfigEmpty: Boolean? = null,
    var kotlinConfig: (ExpectedKotlinConfig.() -> Unit)? = null,
)
fun diffModuleDefinition(given: ModuleDefinition, expectedInit: ExpectedModuleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedModuleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffModuleName(given.getName(), it) != "") { result.add(diffModuleName(given.getName(), it, "${path}name.")) }
    }

    expected.simpleCustomTypes?.let {
        if (given.getSimpleCustomTypes().size != it.size) { result.add("${path}simpleCustomTypes size ${given.getSimpleCustomTypes().size} != ${it.size}"); return@let }
        given.getSimpleCustomTypes().forEachIndexed { idx, entry -> if (diffSimpleStructureDefinition(entry, it[idx]) != "") { result.add(diffSimpleStructureDefinition(entry, it[idx], "${path}simpleCustomTypes[${idx}].")) } }
    }

    expected.complexCustomTypes?.let {
        if (given.getComplexCustomTypes().size != it.size) { result.add("${path}complexCustomTypes size ${given.getComplexCustomTypes().size} != ${it.size}"); return@let }
        given.getComplexCustomTypes().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}complexCustomTypes[${idx}].")) } }
    }

    expected.simpleValueObjects?.let {
        if (given.getSimpleValueObjects().size != it.size) { result.add("${path}simpleValueObjects size ${given.getSimpleValueObjects().size} != ${it.size}"); return@let }
        given.getSimpleValueObjects().forEachIndexed { idx, entry -> if (diffSimpleStructureDefinition(entry, it[idx]) != "") { result.add(diffSimpleStructureDefinition(entry, it[idx], "${path}simpleValueObjects[${idx}].")) } }
    }

    expected.complexValueObjects?.let {
        if (given.getComplexValueObjects().size != it.size) { result.add("${path}complexValueObjects size ${given.getComplexValueObjects().size} != ${it.size}"); return@let }
        given.getComplexValueObjects().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}complexValueObjects[${idx}].")) } }
    }

    expected.dataClasses?.let {
        if (given.getDataClasses().size != it.size) { result.add("${path}dataClasses size ${given.getDataClasses().size} != ${it.size}"); return@let }
        given.getDataClasses().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}dataClasses[${idx}].")) } }
    }

    expected.interfaces?.let {
        if (given.getInterfaces().size != it.size) { result.add("${path}interfaces size ${given.getInterfaces().size} != ${it.size}"); return@let }
        given.getInterfaces().forEachIndexed { idx, entry -> if (diffInterfaceDefinition(entry, it[idx]) != "") { result.add(diffInterfaceDefinition(entry, it[idx], "${path}interfaces[${idx}].")) } }
    }

    expected.propertyKeys?.let {
        if (given.getPropertyKeys().size != it.size) { result.add("${path}propertyKeys size ${given.getPropertyKeys().size} != ${it.size}"); return@let }
        given.getPropertyKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}propertyKeys[${idx}].")) } }
    }

    expected.dataKeys?.let {
        if (given.getDataKeys().size != it.size) { result.add("${path}dataKeys size ${given.getDataKeys().size} != ${it.size}"); return@let }
        given.getDataKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}dataKeys[${idx}].")) } }
    }

    expected.enums?.let {
        if (given.getEnums().size != it.size) { result.add("${path}enums size ${given.getEnums().size} != ${it.size}"); return@let }
        given.getEnums().forEachIndexed { idx, entry -> if (diffEnumDefinition(entry, it[idx]) != "") { result.add(diffEnumDefinition(entry, it[idx], "${path}enums[${idx}].")) } }
    }

    expected.externalTypes?.let {
        if (given.getExternalTypes().size != it.size) { result.add("${path}externalTypes size ${given.getExternalTypes().size} != ${it.size}"); return@let }
        given.getExternalTypes().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}externalTypes[${idx}] ${entry} != ${it[idx]}") } }
    }

    expected.implSubmoduleEmpty?.let {
        if ((given.getImplSubmodule() == null) != it) { result.add("${path}implSubmodule empty ${(given.getImplSubmodule() == null)} != ${it}") }
    }

    expected.implSubmodule?.let {
        if (diffImplSubmoduleDefinition(given.getImplSubmodule()!!, it) != "") { result.add(diffImplSubmoduleDefinition(given.getImplSubmodule()!!, it, "${path}implSubmodule.")) }
    }

    expected.webSubmoduleEmpty?.let {
        if ((given.getWebSubmodule() == null) != it) { result.add("${path}webSubmodule empty ${(given.getWebSubmodule() == null)} != ${it}") }
    }

    expected.webSubmodule?.let {
        if (diffWebSubmoduleDefinition(given.getWebSubmodule()!!, it) != "") { result.add(diffWebSubmoduleDefinition(given.getWebSubmodule()!!, it, "${path}webSubmodule.")) }
    }

    expected.viewModelSubmoduleEmpty?.let {
        if ((given.getViewModelSubmodule() == null) != it) { result.add("${path}viewModelSubmodule empty ${(given.getViewModelSubmodule() == null)} != ${it}") }
    }

    expected.viewModelSubmodule?.let {
        if (diffViewModelSubmoduleDefinition(given.getViewModelSubmodule()!!, it) != "") { result.add(diffViewModelSubmoduleDefinition(given.getViewModelSubmodule()!!, it, "${path}viewModelSubmodule.")) }
    }

    expected.kotlinConfigEmpty?.let {
        if ((given.getKotlinConfig() == null) != it) { result.add("${path}kotlinConfig empty ${(given.getKotlinConfig() == null)} != ${it}") }
    }

    expected.kotlinConfig?.let {
        if (diffKotlinConfig(given.getKotlinConfig()!!, it) != "") { result.add(diffKotlinConfig(given.getKotlinConfig()!!, it, "${path}kotlinConfig.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedTypeDefinition(
    var name: String? = null,
    var wrappers: List<String>? = null,
)
fun diffTypeDefinition(given: TypeDefinition, expectedInit: ExpectedTypeDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedTypeDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.wrappers?.let {
        if (given.getWrappers().size != it.size) { result.add("${path}wrappers size ${given.getWrappers().size} != ${it.size}"); return@let }
        given.getWrappers().forEachIndexed { idx, entry -> if (diffTypeWrapper(entry, it[idx]) != "") { result.add(diffTypeWrapper(entry, it[idx], "${path}wrappers[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedFieldDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
    var defaultValueEmpty: Boolean? = null,
    var defaultValue: String? = null,
)
fun diffFieldDefinition(given: FieldDefinition, expectedInit: ExpectedFieldDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedFieldDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}"); return@let }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    expected.defaultValueEmpty?.let {
        if ((given.getDefaultValue() == null) != it) { result.add("${path}defaultValue empty ${(given.getDefaultValue() == null)} != ${it}") }
    }

    expected.defaultValue?.let {
        if (given.getDefaultValue()!! != it) { result.add("${path}defaultValue ${given.getDefaultValue()!!} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedAttribute(
    var name: String? = null,
    var value: String? = null,
)
fun diffAttribute(given: Attribute, expectedInit: ExpectedAttribute.() -> Unit, path: String = ""): String {
    val expected = ExpectedAttribute().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.value?.let {
        if (given.getValue() != it) { result.add("${path}value ${given.getValue()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSimpleStructureDefinition(
    var name: String? = null,
    var typeName: String? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
)
fun diffSimpleStructureDefinition(given: SimpleStructureDefinition, expectedInit: ExpectedSimpleStructureDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedSimpleStructureDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.typeName?.let {
        if (given.getTypeName() != it) { result.add("${path}typeName ${given.getTypeName()} != ${it}") }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}"); return@let }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedComplexStructureDefinition(
    var name: String? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun diffComplexStructureDefinition(given: ComplexStructureDefinition, expectedInit: ExpectedComplexStructureDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedComplexStructureDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}"); return@let }
        given.getFields().forEachIndexed { idx, entry -> if (diffFieldDefinition(entry, it[idx]) != "") { result.add(diffFieldDefinition(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedInterfaceDefinition(
    var name: String? = null,
    var methods: List<(ExpectedMethodDefinition.() -> Unit)>? = null,
)
fun diffInterfaceDefinition(given: InterfaceDefinition, expectedInit: ExpectedInterfaceDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedInterfaceDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.methods?.let {
        if (given.getMethods().size != it.size) { result.add("${path}methods size ${given.getMethods().size} != ${it.size}"); return@let }
        given.getMethods().forEachIndexed { idx, entry -> if (diffMethodDefinition(entry, it[idx]) != "") { result.add(diffMethodDefinition(entry, it[idx], "${path}methods[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedArgumentDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun diffArgumentDefinition(given: ArgumentDefinition, expectedInit: ExpectedArgumentDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedArgumentDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedExceptionDefinition(
    var name: String? = null,
)
fun diffExceptionDefinition(given: ExceptionDefinition, expectedInit: ExpectedExceptionDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedExceptionDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedMethodDefinition(
    var name: String? = null,
    var returnType: (ExpectedTypeDefinition.() -> Unit)? = null,
    var args: List<(ExpectedArgumentDefinition.() -> Unit)>? = null,
    var throws: List<(ExpectedExceptionDefinition.() -> Unit)>? = null,
)
fun diffMethodDefinition(given: MethodDefinition, expectedInit: ExpectedMethodDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedMethodDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.returnType?.let {
        if (diffTypeDefinition(given.getReturnType(), it) != "") { result.add(diffTypeDefinition(given.getReturnType(), it, "${path}returnType.")) }
    }

    expected.args?.let {
        if (given.getArgs().size != it.size) { result.add("${path}args size ${given.getArgs().size} != ${it.size}"); return@let }
        given.getArgs().forEachIndexed { idx, entry -> if (diffArgumentDefinition(entry, it[idx]) != "") { result.add(diffArgumentDefinition(entry, it[idx], "${path}args[${idx}].")) } }
    }

    expected.throws?.let {
        if (given.getThrows().size != it.size) { result.add("${path}throws size ${given.getThrows().size} != ${it.size}"); return@let }
        given.getThrows().forEachIndexed { idx, entry -> if (diffExceptionDefinition(entry, it[idx]) != "") { result.add(diffExceptionDefinition(entry, it[idx], "${path}throws[${idx}].")) } }
    }

    return result.joinToString("\n")
}