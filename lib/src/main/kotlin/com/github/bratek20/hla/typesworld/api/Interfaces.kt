// DO NOT EDIT! Autogenerated by HLA tool

package com.github.bratek20.hla.typesworld.api

interface TypesWorldApi {
    @Throws(
        SameNameTypeExistsException::class,
    )
    fun ensureType(type: WorldType): Unit

    fun hasType(type: WorldType): Boolean

    fun hasTypeByName(name: WorldTypeName): Boolean

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getTypeByName(name: WorldTypeName): WorldType

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getTypeInfo(type: WorldType): WorldTypeInfo

    fun addPrimitiveType(type: WorldType): Unit

    fun addClassType(type: WorldClassType): Unit

    fun addConcreteParametrizedClass(type: WorldConcreteParametrizedClass): Unit

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getClassType(type: WorldType): WorldClassType

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getConcreteParametrizedClass(type: WorldType): WorldConcreteParametrizedClass

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getConcreteWrapper(type: WorldType): WorldConcreteWrapper

    @Throws(
        WorldTypeNotFoundException::class,
    )
    fun getTypeDependencies(type: WorldType): List<WorldType>

    fun getAllTypes(): List<WorldType>

    fun getAllReferencesOf(target: WorldType, searchFor: WorldType): List<com.github.bratek20.architecture.structs.api.StructPath>

    fun getAllClassTypes(): List<WorldClassType>
}