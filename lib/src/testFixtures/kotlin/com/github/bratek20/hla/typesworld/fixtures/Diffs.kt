// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.typesworld.fixtures

import com.github.bratek20.hla.typesworld.api.*

fun diffWorldTypeName(given: WorldTypeName, expected: String, path: String = ""): String {
    if (given.value != expected) { return "${path}value ${given.value} != ${expected}" }
    return ""
}

fun diffWorldTypePath(given: WorldTypePath, expected: String, path: String = ""): String {
    if (worldTypePathGetValue(given) != expected) { return "${path}value ${worldTypePathGetValue(given)} != ${expected}" }
    return ""
}

fun diffWorldTypeKind(given: WorldTypeKind, expected: String, path: String = ""): String {
    if (given != WorldTypeKind.valueOf(expected)) { return "${path}value ${given.name} != ${expected}" }
    return ""
}

data class ExpectedWorldType(
    var name: String? = null,
    var path: String? = null,
)
fun diffWorldType(given: WorldType, expectedInit: ExpectedWorldType.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldType().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffWorldTypeName(given.getName(), it) != "") { result.add(diffWorldTypeName(given.getName(), it, "${path}name.")) }
    }

    expected.path?.let {
        if (diffWorldTypePath(given.getPath(), it) != "") { result.add(diffWorldTypePath(given.getPath(), it, "${path}path.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedWorldClassField(
    var name: String? = null,
    var type: (ExpectedWorldType.() -> Unit)? = null,
)
fun diffWorldClassField(given: WorldClassField, expectedInit: ExpectedWorldClassField.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldClassField().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffWorldType(given.getType(), it) != "") { result.add(diffWorldType(given.getType(), it, "${path}type.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedWorldClassType(
    var type: (ExpectedWorldType.() -> Unit)? = null,
    var fields: List<(ExpectedWorldClassField.() -> Unit)>? = null,
    var extendsEmpty: Boolean? = null,
    var extends: (ExpectedWorldType.() -> Unit)? = null,
)
fun diffWorldClassType(given: WorldClassType, expectedInit: ExpectedWorldClassType.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldClassType().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.type?.let {
        if (diffWorldType(given.getType(), it) != "") { result.add(diffWorldType(given.getType(), it, "${path}type.")) }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}"); return@let }
        given.getFields().forEachIndexed { idx, entry -> if (diffWorldClassField(entry, it[idx]) != "") { result.add(diffWorldClassField(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    expected.extendsEmpty?.let {
        if ((given.getExtends() == null) != it) { result.add("${path}extends empty ${(given.getExtends() == null)} != ${it}") }
    }

    expected.extends?.let {
        if (diffWorldType(given.getExtends()!!, it) != "") { result.add(diffWorldType(given.getExtends()!!, it, "${path}extends.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedWorldConcreteWrapper(
    var type: (ExpectedWorldType.() -> Unit)? = null,
    var wrappedType: (ExpectedWorldType.() -> Unit)? = null,
)
fun diffWorldConcreteWrapper(given: WorldConcreteWrapper, expectedInit: ExpectedWorldConcreteWrapper.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldConcreteWrapper().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.type?.let {
        if (diffWorldType(given.getType(), it) != "") { result.add(diffWorldType(given.getType(), it, "${path}type.")) }
    }

    expected.wrappedType?.let {
        if (diffWorldType(given.getWrappedType(), it) != "") { result.add(diffWorldType(given.getWrappedType(), it, "${path}wrappedType.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedWorldConcreteParametrizedClass(
    var type: (ExpectedWorldType.() -> Unit)? = null,
    var typeArguments: List<(ExpectedWorldType.() -> Unit)>? = null,
)
fun diffWorldConcreteParametrizedClass(given: WorldConcreteParametrizedClass, expectedInit: ExpectedWorldConcreteParametrizedClass.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldConcreteParametrizedClass().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.type?.let {
        if (diffWorldType(given.getType(), it) != "") { result.add(diffWorldType(given.getType(), it, "${path}type.")) }
    }

    expected.typeArguments?.let {
        if (given.getTypeArguments().size != it.size) { result.add("${path}typeArguments size ${given.getTypeArguments().size} != ${it.size}"); return@let }
        given.getTypeArguments().forEachIndexed { idx, entry -> if (diffWorldType(entry, it[idx]) != "") { result.add(diffWorldType(entry, it[idx], "${path}typeArguments[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedWorldTypeInfo(
    var kind: String? = null,
)
fun diffWorldTypeInfo(given: WorldTypeInfo, expectedInit: ExpectedWorldTypeInfo.() -> Unit, path: String = ""): String {
    val expected = ExpectedWorldTypeInfo().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.kind?.let {
        if (diffWorldTypeKind(given.getKind(), it) != "") { result.add(diffWorldTypeKind(given.getKind(), it, "${path}kind.")) }
    }

    return result.joinToString("\n")
}
fun diffStructPath(given: com.github.bratek20.architecture.structs.api.StructPath, expected: com.github.bratek20.architecture.structs.api.StructPath, path: String = ""): String {
    if (given != expected) { return "${path}value ${given} != ${expected}" }
    return ""
}
