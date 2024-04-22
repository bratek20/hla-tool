package pl.bratek20.hla.generation.impl.languages.typescript

import pl.bratek20.hla.generation.impl.core.domain.HlaModules
import pl.bratek20.hla.generation.impl.core.language.LanguageTypes
import pl.bratek20.hla.generation.impl.core.language.MoreLanguageTypes
import pl.bratek20.hla.model.BaseType
import pl.bratek20.hla.utils.pascalToCamelCase

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

    override fun mapListElements(listName: String, elementName: String, mapping: String): String {
        return "($listName).map($elementName => $mapping)"
    }

    override fun classConstructor(name: String, params: String): String {
        return "new $name($params)"
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

    override fun indentionForAssertListElements(): Int {
        return 12
    }
}

class TypeScriptMoreTypes(modules: HlaModules) : MoreLanguageTypes(modules) {
    override fun assertFunName(name: String): String {
        return pascalToCamelCase(name)
    }

    override fun defClassType(name: String): String {
        val base = "${name}Def"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Builder.$base"
        }
    }

    override fun expectedClassType(name: String): String {
        val base = "Expected${name}"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Assert.$base"
        }
    }

    override fun complexVoAssertion(name: String, given: String, expected: String): String {
        val base = "${pascalToCamelCase(name)}($given, $expected)"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Assert.$base"
        }
    }

    override fun complexVoDefConstructor(name: String, arg: String): String {
        val base = "${pascalToCamelCase(name)}($arg)"
        val module = modules.getComplexVoModule(name);
        return if (module == modules.current.name) {
            base
        } else {
            "${module.value}.Builder.$base"
        }
    }
}