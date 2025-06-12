package com.github.bratek20.hla.typesworld.api

fun WorldClassType.getField(name: String): WorldClassField {
    return this.getFields().first { it.getName() == name }
}

fun TypesWorldApi.findByName(name: WorldTypeName): WorldType? {
    return this.hasTypeByName(name)
        .takeIf { it }
        ?.let { this.getTypeByName(name) }
}