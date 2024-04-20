package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.model.BaseType

class KotlinTypes: LanguageTypes {
    override fun mapBaseType(type: BaseType): String {
        return when (type) {
            BaseType.STRING -> "String"
            BaseType.INT -> "Int"
            BaseType.BOOL -> "Boolean"
            BaseType.VOID -> "Unit"
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
        return "List<$typeName>"
    }

    override fun defaultValueForList(): String {
        return "emptyList()"
    }

    override fun mapList(variableName: String, mapping: String): String {
        //expected: "$variableName.map { ${mapping("it")} }"
        return "$variableName.map { $mapping }"

    }

    override fun classConstructor(name: String, params: String): String {
        return "$name($params)"
    }

    override fun defClassType(name: String): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun assertEquals(given: String, expected: String): String {
        return "assertThat($given).isEqualTo($expected)"
    }

    override fun assertArraysLength(given: String, expected: String): String {
        return "assertThat($given).hasSize($expected.size)"
    }

    override fun arrayIndexedIteration(array: String, body: (it: String, index: String) -> String): String {
        return "$array.forEachIndexed { index, entry -> ${body("entry", "index")} }"
    }
}