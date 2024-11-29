package com.github.bratek20.hla.typesworld.api

data class WorldTypePath(
    val value: String
){
    fun asParts() = value.split("/")

    override fun toString(): String {
        return value
    }
}