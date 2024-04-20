package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.model.BaseType

class TypeScriptTypes: LanguageTypes {
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

    override fun mapList(variableName: String, mapping: String): String {
        //expected: "$variableName.map { ${mapping("it")} }"
        return "($variableName).map(it => $mapping)"

    }

    override fun classConstructor(name: String, params: String): String {
        return "new $name($params)"
    }

    override fun defClassType(name: String): String {
        return "${name}Def"
    }

    override fun assertEquals(given: String, expected: String): String {
        return "AssertEquals($given, $expected)"
    }

    override fun assertArraysLength(given: String, expected: String): String {
        return assertEquals("$given.length", "$expected.length")
    }

    override fun arrayIndexedIteration(array: String, idx: String, entry: String, body: String): String {
        return "$array.forEach(($entry, $idx) => $body)"
    }
}