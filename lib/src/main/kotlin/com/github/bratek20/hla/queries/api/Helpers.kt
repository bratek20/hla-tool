package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.typesworld.api.WorldTypeName

fun TypeDefinition.asWorldTypeName(): WorldTypeName {
    if (this.getWrappers().contains(TypeWrapper.LIST)) {
        return WorldTypeName("List<${this.getName()}>")
    }
    if (this.getWrappers().contains(TypeWrapper.OPTIONAL)) {
        return WorldTypeName("Optional<${this.getName()}>")
    }
    return WorldTypeName(this.getName())
}

fun WorldTypeName.asTypeDefinition(): TypeDefinition {
    val name = this.value
    if (name.contains("List<") && name.contains("Optional<")) {
        throw IllegalArgumentException("Unsupported mapping for '$name' (both list and optional)")
    }

    val unwrappedName = name.removePrefix("List<").removeSuffix(">")
        .removePrefix("Optional<").removeSuffix(">")
    return TypeDefinition.create(
        name = unwrappedName,
        wrappers = when {
            name.startsWith("List<") && name.endsWith(">") -> listOf(TypeWrapper.LIST)
            name.startsWith("Optional<") && name.endsWith(">") -> listOf(TypeWrapper.OPTIONAL)
            else -> emptyList()
        }
    )
}