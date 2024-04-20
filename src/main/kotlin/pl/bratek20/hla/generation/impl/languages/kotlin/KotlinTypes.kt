package pl.bratek20.hla.generation.impl.languages.kotlin

import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.domain.LanguageTypes
import pl.bratek20.hla.generation.impl.core.domain.MoreLanguageTypes
import pl.bratek20.hla.model.BaseType
import pl.bratek20.hla.utils.pascalToCamelCase

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

    override fun mapListElements(listName: String, elementName: String, mapping: String): String {
        return "$listName.map { $elementName -> $mapping }"
    }

    override fun classConstructor(name: String, params: String): String {
        return "$name($params)"
    }

    override fun assertEquals(given: String, expected: String): String {
        return "assertThat($given).isEqualTo($expected)"
    }

    override fun assertListLength(given: String, expected: String): String {
        return "assertThat($given).hasSize($expected.size)"
    }

    override fun listIndexedIteration(listName: String, idx: String, entry: String, body: String): String {
        return "$listName.forEachIndexed { $idx, $entry -> $body }"
    }

    override fun indentionForAssertListElements(): Int {
        return 8
    }
}

class KotlinMoreTypes(modules: HlaModules) : MoreLanguageTypes(modules) {
    override fun defClassType(name: String): String {
        return "(${name}Def.() -> Unit)"
    }

    override fun expectedClassType(name: String): String {
        return "(Expected${name}.() -> Unit)"
    }

    override fun complexVoAssertion(name: String, given: String, expected: String): String {
        return "assert$name($given, $expected)"
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        return "${pascalToCamelCase(name)}($arg)"
    }
}