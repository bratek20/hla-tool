// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.typesworld.fixtures

import com.github.bratek20.hla.typesworld.api.*

fun worldTypeName(value: String = "someValue"): WorldTypeName {
    return WorldTypeName(value)
}

fun worldTypePath(value: String = "Some/Path/To/Type"): WorldTypePath {
    return worldTypePathCreate(value)
}

data class WorldTypeDef(
    var name: String = "someValue",
    var path: String = "Some/Path/To/Type",
)
fun worldType(init: WorldTypeDef.() -> Unit = {}): WorldType {
    val def = WorldTypeDef().apply(init)
    return WorldType.create(
        name = WorldTypeName(def.name),
        path = worldTypePathCreate(def.path),
    )
}

data class WorldClassFieldDef(
    var name: String = "someValue",
    var type: (WorldTypeDef.() -> Unit) = {},
)
fun worldClassField(init: WorldClassFieldDef.() -> Unit = {}): WorldClassField {
    val def = WorldClassFieldDef().apply(init)
    return WorldClassField.create(
        name = def.name,
        type = worldType(def.type),
    )
}

data class WorldClassTypeDef(
    var type: (WorldTypeDef.() -> Unit) = {},
    var fields: List<(WorldClassFieldDef.() -> Unit)> = emptyList(),
    var extends: (WorldTypeDef.() -> Unit)? = null,
)
fun worldClassType(init: WorldClassTypeDef.() -> Unit = {}): WorldClassType {
    val def = WorldClassTypeDef().apply(init)
    return WorldClassType.create(
        type = worldType(def.type),
        fields = def.fields.map { it -> worldClassField(it) },
        extends = def.extends?.let { it -> worldType(it) },
    )
}

data class WorldConcreteWrapperDef(
    var type: (WorldTypeDef.() -> Unit) = {},
    var wrappedType: (WorldTypeDef.() -> Unit) = {},
)
fun worldConcreteWrapper(init: WorldConcreteWrapperDef.() -> Unit = {}): WorldConcreteWrapper {
    val def = WorldConcreteWrapperDef().apply(init)
    return WorldConcreteWrapper.create(
        type = worldType(def.type),
        wrappedType = worldType(def.wrappedType),
    )
}

data class WorldConcreteParametrizedClassDef(
    var type: (WorldTypeDef.() -> Unit) = {},
    var typeArguments: List<(WorldTypeDef.() -> Unit)> = emptyList(),
)
fun worldConcreteParametrizedClass(init: WorldConcreteParametrizedClassDef.() -> Unit = {}): WorldConcreteParametrizedClass {
    val def = WorldConcreteParametrizedClassDef().apply(init)
    return WorldConcreteParametrizedClass.create(
        type = worldType(def.type),
        typeArguments = def.typeArguments.map { it -> worldType(it) },
    )
}

data class WorldTypeInfoDef(
    var kind: String = WorldTypeKind.Primitive.name,
)
fun worldTypeInfo(init: WorldTypeInfoDef.() -> Unit = {}): WorldTypeInfo {
    val def = WorldTypeInfoDef().apply(init)
    return WorldTypeInfo.create(
        kind = WorldTypeKind.valueOf(def.kind),
    )
}