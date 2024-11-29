package com.github.bratek20.hla.hlatypesworld.impl

import com.github.bratek20.hla.definitions.api.ComplexStructureDefinition
import com.github.bratek20.hla.definitions.api.EnumDefinition
import com.github.bratek20.hla.definitions.api.ModuleDefinition
import com.github.bratek20.hla.definitions.api.SimpleStructureDefinition
import com.github.bratek20.hla.generation.api.PatternName
import com.github.bratek20.hla.generation.api.SubmoduleName
import com.github.bratek20.hla.hlatypesworld.api.HlaTypePath
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldPopulator
import com.github.bratek20.hla.hlatypesworld.api.asWorld
import com.github.bratek20.hla.queries.api.*
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldClassType
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName

abstract class ApiPatternPopulator {
    private lateinit var module: ModuleDefinition
    protected lateinit var world: TypesWorldApi

    fun init(module: ModuleDefinition, world: TypesWorldApi) {
        this.module = module
        this.world = world
    }

    protected abstract fun getTypeNames(): List<String>
    protected abstract fun getPatternName(): PatternName

    fun ensurePatternTypes() {
        getTypeNames().forEach { typeName ->
            world.ensureType(getMyPatternType(typeName))
        }
    }

    protected fun getMyPatternType(typeName: String): WorldType {
        val path = HlaTypePath.create(
            module.getName(),
            SubmoduleName.Api,
            getPatternName()
        ).asWorld()
        return WorldType.create(
            name = WorldTypeName(typeName),
            path = path
        )
    }

    open fun addPatternTypes() {
        //no-op
    }
}

abstract class SimpleStructurePopulator(
    private val defs: List<SimpleStructureDefinition>
): ApiPatternPopulator() {
    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }

    override fun addPatternTypes() {
        defs.forEach { def ->
            world.addClassType(
                WorldClassType.create(
                type = getMyPatternType(def.getName()),
                fields = listOf(
                    createFieldDefinition("value", def.getTypeName())
                        .asClassField(world)
                )
            ))
        }
    }
}

class SimpleValueObjectsPopulator(
    defs: List<SimpleStructureDefinition>
): SimpleStructurePopulator(defs) {
    override fun getPatternName() = PatternName.ValueObjects
}

class SimpleCustomTypesPopulator(
    defs: List<SimpleStructureDefinition>
): SimpleStructurePopulator(defs) {
    override fun getPatternName() = PatternName.CustomTypes
}

abstract class ComplexStructuresPopulator(
    private val defs: List<ComplexStructureDefinition>
): ApiPatternPopulator() {
    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }

    override fun addPatternTypes() {
        defs.forEach { def ->
            world.addClassType(
                WorldClassType.create(
                type = getMyPatternType(def.getName()),
                fields = def.getFields().map { it.asClassField(world) }
            ))
        }
    }
}

class ComplexValueObjectsPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.ValueObjects
}

class ComplexCustomTypesPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.CustomTypes
}

class DataClassesPopulator(
    defs: List<ComplexStructureDefinition>
): ComplexStructuresPopulator(defs) {
    override fun getPatternName() = PatternName.DataClasses
}

class EnumsPopulator(
    private val defs: List<EnumDefinition>
): ApiPatternPopulator() {
    override fun getPatternName() = PatternName.Enums

    override fun getTypeNames(): List<String> {
        return defs.map { it.getName() }
    }
}

//TODO-FIX current abstraction is not suited for external types i.e they do not have pattern name
class ExternalTypesPopulator(
    private val defs: List<String>
): ApiPatternPopulator() {
    //TODO-FIX
    override fun getPatternName() = PatternName.ValueObjects

    override fun getTypeNames(): List<String> {
        return defs
    }
}

class ApiTypesPopulator(
    private val world: TypesWorldApi
): HlaTypesWorldPopulator {
    companion object {
        const val ORDER = 1
    }
    override fun getOrder(): Int {
        return ORDER
    }


    private fun createPatternPopulators(module: ModuleDefinition): List<ApiPatternPopulator> {
        val populators = listOf(
            SimpleValueObjectsPopulator(module.getSimpleValueObjects()),
            ComplexValueObjectsPopulator(module.getComplexValueObjects()),

            SimpleCustomTypesPopulator(module.getSimpleCustomTypes()),
            ComplexCustomTypesPopulator(module.getComplexCustomTypes()),

            DataClassesPopulator(module.getDataClasses()),

            EnumsPopulator(module.getEnums()),

            ExternalTypesPopulator(module.getExternalTypes())
        )

        populators.forEach { populator ->
            populator.init(module, world)
        }

        return populators
    }

    override fun populate(modules: List<ModuleDefinition>) {
        val populators = modules.flatMap { createPatternPopulators(it) }

        populators.forEach { it.ensurePatternTypes() }
        populators.forEach { it.addPatternTypes() }
    }
}