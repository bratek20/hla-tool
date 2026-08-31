package com.github.bratek20.hla.typesworld.api

fun TypesWorldApi.findByName(name: WorldTypeName): WorldType? {
    return this.hasTypeByName(name)
        .takeIf { it }
        ?.let { this.getTypeByName(name) }
}