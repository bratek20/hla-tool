package com.github.bratek20.hla.generation.impl.languages.csharp

import com.github.bratek20.codebuilder.core.CSharp
import com.github.bratek20.codebuilder.core.CodeBuilderContext
import com.github.bratek20.hla.generation.impl.core.language.LanguageTypes
import com.github.bratek20.hla.definitions.api.BaseType
import com.github.bratek20.hla.queries.api.ModuleGroupQueries
import com.github.bratek20.utils.camelToPascalCase
import com.github.bratek20.utils.pascalToCamelCase

class CSharpTypes(): LanguageTypes {
    override fun publicComplexStructureFieldPrefix(): String {
        return ""
    }

    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "string"
            BaseType.INT -> "int"
            BaseType.BOOL -> "bool"
            BaseType.VOID -> "void"
            BaseType.ANY -> "object"
            BaseType.DOUBLE -> "double"
            BaseType.LONG -> "long"
            BaseType.STRUCT -> "object"
        }
    }

    override fun defaultValueForBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "\"someValue\""
            BaseType.INT -> "0"
            BaseType.BOOL -> "false"
            BaseType.VOID -> throw IllegalArgumentException("Void type has no default value")
            BaseType.ANY -> "{}"
            BaseType.DOUBLE -> "0"
            BaseType.LONG -> "0"
            BaseType.STRUCT -> "{}"
        }
    }

    override fun wrapWithList(typeName: String): String {
        return "$typeName[]"
    }

    override fun defaultValueForList(): String {
        return "[]"
    }

    override fun mapListElements(listName: String, elementName: String, mapping: String): String {
        val finalListName = if (listName.contains(" ")) "($listName)" else listName
        return "$finalListName.map($elementName => $mapping)"
    }

    override fun addListElement(listName: String, elementName: String): String {
        return "$listName.push($elementName)"
    }

    override fun listSize(listName: String): String {
        return "$listName.length"
    }

    override fun wrapWithString(value: String): String {
        return "`$value`"
    }

    override fun undefinedValue(): String {
        return "undefined"
    }

    override fun wrapWithOptional(typeName: String): String {
        return "Optional<$typeName>"
    }

    override fun wrapWithSoftOptional(typeName: String): String {
        return typeName
    }

    override fun mapOptionalElement(optionalName: String, elementName: String, mapping: String): String {
        return "$optionalName.map($elementName => $mapping)"
    }

    override fun unwrapOptional(variableName: String): String {
        return "$variableName.get()"
    }

    override fun serializeOptional(variableName: String): String {
        return "$variableName.orElse(undefined)"
    }

    override fun deserializeOptional(variableName: String): String {
        return "Optional.of($variableName)"
    }

    override fun emptyOptional(): String {
        return "Optional.empty()"
    }

    override fun checkOptionalEmpty(variableName: String): String {
        return "$variableName.isEmpty()"
    }

    override fun classConstructorCall(className: String): String {
        return "new $className"
    }

    override fun assertEquals(given: String, expected: String): String {
        return "AssertEquals($given, $expected)"
    }

    override fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String {
        return "$listName.forEach(($entry, $idx) => { $body })"
    }

    override fun deserializeEnum(enumName: String, variable: String): String {
        return "(${enumName})Enum.Parse(typeof($enumName), $variable)"
    }

    override fun serializeEnum(variableName: String): String {
        return "$variableName.ToString()"
    }

    override fun propertyClassConstructorCall(className: String): String {
        return "$className.create"
    }

    override fun customTypeConstructorName(className: String): String {
        return "${pascalToCamelCase(className)}Create"
    }

    override fun customTypeConstructorCall(className: String): String {
        return "TODO"
    }

    override fun customTypeGetterName(className: String, fieldName: String): String {
        return "${pascalToCamelCase(className)}Get${camelToPascalCase(fieldName)}"
    }

    override fun customTypeGetterCall(className: String, fieldName: String): String {
        return "TODO"
    }

    override fun context(): CodeBuilderContext {
        return CodeBuilderContext(CSharp())
    }
}

