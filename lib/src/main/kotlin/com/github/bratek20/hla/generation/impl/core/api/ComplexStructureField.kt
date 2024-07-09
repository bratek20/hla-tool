package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.hla.definitions.api.ComplexStructureDefinition
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.hla.generation.impl.languages.typescript.ObjectCreationMapper
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptTypes
import com.github.bratek20.hla.utils.camelToPascalCase

open class ComplexStructureField(
    protected val def: FieldDefinition,
    private val factory: ApiTypeFactory
) {
    private lateinit var complexStructure: ComplexStructureApiType<*>

    fun init(complexStructure: ComplexStructureApiType<*>) {
        this.complexStructure = complexStructure
    }

    val name = def.getName()

    val type: ApiType by lazy {
        factory.create(def.getType())
    }

    fun access(variableName: String): String {
        if (complexStructure is ComplexCustomApiType) {
            return accessComplexCustomType(variableName)
        }
        return "$variableName.${getterName()}()"
    }

    private fun accessComplexCustomType(variableName: String): String {
        return type.languageTypes.customTypeGetterCall(complexStructure.name, name) + "($variableName)"
    }

    fun exampleValue(): String? {
        return def.getAttributes().firstOrNull { it.getName() == "example" }?.getValue()
    }

    fun defaultValue(): String? {
        def.getDefaultValue()?.let {
            return mapDefaultValue(it)
        }
        return null
    }

    // used by velocity
    fun accessor(): String {
        val isPublic = def.getAttributes().any { it.getName() == "public" }
        val prefix = type.languageTypes.publicComplexStructureFieldPrefix()
        val finalPrefix = if (prefix.isNotEmpty()) "$prefix " else ""
        return if(isPublic) finalPrefix else "private "
    }

    // used by velocity
    fun classDeclaration(): String {
        if (type.languageTypes is TypeScriptTypes) {
            val oc = ObjectCreationMapper()
            return "${accessor()}${privateName()}${oc.adjustAssignment(type.serializableName())} = ${oc.map(type.serializableName())}"
        }
        val valOrVar = if (complexStructure is DataClassApiType) "var" else "val"
        val base = "${accessor()}${valOrVar} ${privateName()}: ${type.serializableName()}"
        defaultValue()?.let {
            return "$base = $it"
        }
        return base
    }

    // used by velocity
    fun createDeclaration(): String {
        val base = "${name}: ${type.name()}"
        defaultValue()?.let {
            return "$base = $it"
        }
        return base
    }

    // used by velocity
    fun createConstructorPass(): String {
        //instance.$field.name = $field.type.serialize($field.name)
        if (type.languageTypes is TypeScriptTypes) {
            return "instance.${privateName()} = ${type.serialize(name)}"
        }
        return "${privateName()} = ${type.serialize(name)}"
    }

    fun privateName(): String {
        def.getAttributes().firstOrNull { it.getName() == "from" }?.let {
            return it.getValue()
        }
        return name
    }

    private fun mapDefaultValue(value: String): String {
        if (value == "[]") {
            return type.languageTypes.defaultValueForList()
        }
        if (value == "empty") {
            return type.languageTypes.emptyOptional()
        }
        return value
    }

    fun getter(): ComplexStructureGetter {
        return ComplexStructureGetter(getterName(), type, privateName())
    }

    //TODO-REF introduce setterBody and setterDeclaration? to simplify velocity
    fun setter(): ComplexStructureSetter {
        return ComplexStructureSetter(
            name = setterName(),
            type = type,
            publicField = name,
            privateField = privateName()
        )
    }

    fun getterName(): String {
        if(type.languageTypes is KotlinTypes) {
            val records = complexStructure.typeModule?.getKotlinConfig()?.getRecords() ?: emptyList()
            if (records.contains(complexStructure.name())) {
                return name
            }
        }
        return "get${camelToPascalCase(name)}"
    }

    fun setterName(): String {
        return "set${camelToPascalCase(name)}"
    }
}