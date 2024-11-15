package com.github.bratek20.hla.typesworld.api

class WorldTypePath(
    val value: String
){
    fun asParts() = value.split("/")
}