package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.definitions.api.BaseType
import pl.bratek20.hla.definitions.impl.HlaModules
import pl.bratek20.hla.utils.camelToPascalCase
import pl.bratek20.hla.utils.pascalToCamelCase

class TypeScriptTypes(private val modules: HlaModules): LanguageTypes {
    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "string"
            BaseType.INT -> "number"
            BaseType.BOOL -> "boolean"
            BaseType.VOID -> "void"
        }
    }

    override fun defaultValueForBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "\"someValue\""
            BaseType.INT -> "0"
            BaseType.BOOL -> "false"
            BaseType.VOID -> throw IllegalArgumentException("Void type has no default value")
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

    override fun defaultValueForDefOptional(): String {
        return "undefined"
    }

    override fun wrapWithOptional(typeName: String): String {
        return "Optional<$typeName>"
    }

    override fun mapOptionalElement(optionalName: String, elementName: String, mapping: String): String {
        return "$optionalName.map($elementName => $mapping)"
    }

    override fun unwrapOptional(variableName: String): String {
        return "$variableName.get()"
    }

    override fun classConstructorCall(className: String): String {
        return "new $className"
    }

    override fun assertEquals(given: String, expected: String): String {
        return "AssertEquals($given, $expected)"
    }

    override fun assertListLength(given: String, expected: String): String {
        return assertEquals("$given.length", "$expected.length")
    }

    override fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String {
        return "$listName.forEach(($entry, $idx) => $body)"
    }

    override fun enumConstructor(enumName: String, variable: String): String {
        return "$enumName.fromName($variable).get()"
    }

    override fun enumGetName(variableName: String): String {
        return "$variableName.getName()"
    }

    override fun propertyClassConstructor(className: String): String {
        return "$className.create"
    }

    override fun customTypeConstructorName(className: String): String {
        return "${pascalToCamelCase(className)}Create"
    }

    override fun customTypeConstructorCall(className: String): String {
        return addModuleNamePrefix(modules, className, "CustomTypesMapper.${customTypeConstructorName(className)}")
    }

    override fun customTypeGetterName(className: String, fieldName: String): String {
        return "${pascalToCamelCase(className)}Get${camelToPascalCase(fieldName)}"
    }

    override fun customTypeGetterCall(className: String, fieldName: String): String {
        return addModuleNamePrefix(modules, className, "CustomTypesMapper.${customTypeGetterName(className, fieldName)}")
    }
}

