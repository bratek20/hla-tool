package com.github.bratek20.hla.queries.api

import com.github.bratek20.hla.definitions.api.TypeDefinition
import com.github.bratek20.hla.definitions.api.TypeWrapper
import com.github.bratek20.hla.typesworld.api.WorldTypeName

fun TypeDefinition.asWorldTypeName(): WorldTypeName {
    var name = this.getName()
    if (this.getWrappers().contains(TypeWrapper.LIST)) {
        name = "List<$name>"
    }
    if (this.getWrappers().contains(TypeWrapper.OPTIONAL)) {
        name = "Optional<$name>"
    }
    return WorldTypeName(name)
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