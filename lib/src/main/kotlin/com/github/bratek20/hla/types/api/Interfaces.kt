// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.types.api

interface TypesApi {
    fun addStructure(structure: Structure): Unit

    fun addWrapper(wrapper: Wrapper): Unit

    fun getTypeDependencies(type: HlaType): List<HlaType>
}