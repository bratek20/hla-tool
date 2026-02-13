package com.github.bratek20.hla.generation.impl.core.api

import com.github.bratek20.architecture.exceptions.ShouldNeverHappenException
import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.builders.nullValue
import com.github.bratek20.codebuilder.types.emptyHardOptional
import com.github.bratek20.codebuilder.types.emptyImmutableList
import com.github.bratek20.hla.apitypes.impl.*
import com.github.bratek20.hla.definitions.api.FieldDefinition
import com.github.bratek20.hla.generation.impl.languages.kotlin.KotlinTypes
import com.github.bratek20.hla.generation.impl.languages.typescript.ObjectCreationMapper
import com.github.bratek20.hla.generation.impl.languages.typescript.TypeScriptTypes
import com.github.bratek20.utils.camelToPascalCase

open class ComplexStructureField(
    val def: FieldDefinition,
    val factory: ApiTypeFactoryLogic
) {
    private lateinit var complexStructure: ComplexStructureApiType<*>

    private val kotlinRestrictedWords = listOf("as")

    fun init(complexStructure: ComplexStructureApiType<*>) {
        this.complexStructure = complexStructure
    }

    val type: ApiTypeLogic by lazy {
        factory.create(def.getType())
    }

    val name : String by lazy {
        val raw = def.getName()
        buildNameFromRaw(raw)
    }

    val defName = def.getName()

    fun access(variableName: String): String {
        if (complexStructure is ComplexCustomApiType) {
            return accessComplexCustomType(variableName)
        }
        return "$variableName.${getterName()}()"
    }

    private fun accessComplexCustomType(variableName: String): String {
        return type.languageTypes.customTypeGetterCall(complexStructure.name, name) + "($variableName)"
    }

    @Deprecated("Use exampleValueBuilder() instead")
    fun exampleValue(): String? {
        return exampleValueBuilder()?.build(factory.languageTypes.context())
    }

    fun exampleValueBuilder(): ExpressionBuilder? {
        return extractExampleValue()?.let {
            expression(it)
        }
    }

    fun getExampleValue(): Any? {
        val value = this.extractExampleValue()
        if(value != null) {
            val typeToParse = extractBaseApiType(type) ?: throw ShouldNeverHappenException("Type")
            return BaseApiType.parseToProperExampleFormat(typeToParse, value)
        }
        return value
    }

    private fun extractBaseApiType(type: ApiTypeLogic): BaseApiType?{
        if(type is BaseApiType) {
            return type as BaseApiType
        }
        if(type is SimpleStructureApiType) {
            return (type as SimpleStructureApiType).boxedType
        }
        if(type is OptionalApiType) {
            return extractBaseApiType((type as OptionalApiType).wrappedType)
        }
        return null
    }

    private fun extractExampleValue(): String? {
        return extractBaseApiType(type)?.let {
            extractExampleValueFromAttributes(it, def.getAttributes())
        }
    }

    @Deprecated("Use defaultValueBuilder() instead")
    fun defaultValue(): String? {
        return defaultValueBuilder()?.build(factory.languageTypes.context())
    }

    fun defaultValueBuilder(): ExpressionBuilder? {
        def.getDefaultValue()?.let {
            return mapDefaultValue(it)
        }
        return null
    }

    @Deprecated("Use defaultSerializedValueBuilder() instead")
    fun defaultSerializedValue(): String? {
        return defaultSerializedValueBuilder()?.build(factory.languageTypes.context())
    }

    fun defaultSerializedValueBuilder(): ExpressionBuilder? {
        def.getDefaultValue()?.let {
            return mapDefaultSerializableValue(it)
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
    fun createDeclaration(): String = internalCreateDeclaration(true)
    fun createDeclarationNoDefault(): String = internalCreateDeclaration(false)

    private fun internalCreateDeclaration(allowDefault: Boolean): String {
        val base = "${name}: ${type.name()}"
        if (allowDefault) {
            defaultValue()?.let {
                return "$base = $it"
            }
        }
        return base
    }

    // used by velocity
    fun createConstructorPass(): String {
        //instance.$field.name = $field.type.serialize($field.name)
        if (type.languageTypes is TypeScriptTypes) {
            return "instance.${privateName()} = ${type.serialize(name)}"
        }
        val assignment = type.serialize(name)
        return "${privateName()} = ${buildNameFromRaw(assignment)}"
    }

    fun buildNameFromRaw(value: String): String {
        if (type.languageTypes is KotlinTypes && kotlinRestrictedWords.contains(value)){
            return "`$value`"
        }
        return value
    }

    fun privateName(): String {
        var finalName = name
        def.getAttributes().firstOrNull { it.getName() == "from" }?.let {
            finalName = it.getValue()
        }
        return buildNameFromRaw(finalName)
    }

    private fun mapDefaultValue(value: String): ExpressionBuilder {
        if (value == "[]") {
            return emptyImmutableList((type as ListApiType).wrappedType.builder())
        }
        if (value == "empty") {
            return emptyHardOptional((type as OptionalApiType).wrappedType.builder())
        }
        return expression(value)
    }

    private fun mapDefaultSerializableValue(value: String): ExpressionBuilder {
        if (value == "[]") {
            return emptyImmutableList((type as ListApiType).wrappedType.serializableBuilder())
        }
        if (value == "empty") {
            return nullValue()
        }
        return expression(value)
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
                return defName
            }
        }
        return "get${camelToPascalCase(defName)}"
    }

    fun setterName(): String {
        return "set${camelToPascalCase(defName)}"
    }
}