// DO NOT EDIT! Autogenerated by HLA tool

package pl.bratek20.hla.definitions.fixtures

import pl.bratek20.hla.facade.api.*
import pl.bratek20.hla.facade.fixtures.*

import pl.bratek20.hla.definitions.api.*

data class ExpectedKeyDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun diffKeyDefinition(given: KeyDefinition, expectedInit: ExpectedKeyDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedKeyDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedEnumDefinition(
    var name: String? = null,
    var values: List<String>? = null,
)
fun diffEnumDefinition(given: EnumDefinition, expectedInit: ExpectedEnumDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedEnumDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.values?.let {
        if (given.getValues().size != it.size) { result.add("${path}values size ${given.getValues().size} != ${it.size}") }
        given.getValues().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}values[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}

data class ExpectedImplSubmoduleDefinition(
    var dataClasses: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var dataKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
)
fun diffImplSubmoduleDefinition(given: ImplSubmoduleDefinition, expectedInit: ExpectedImplSubmoduleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedImplSubmoduleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.dataClasses?.let {
        if (given.getDataClasses().size != it.size) { result.add("${path}dataClasses size ${given.getDataClasses().size} != ${it.size}") }
        given.getDataClasses().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}dataClasses[${idx}].")) } }
    }

    expected.dataKeys?.let {
        if (given.getDataKeys().size != it.size) { result.add("${path}dataKeys size ${given.getDataKeys().size} != ${it.size}") }
        given.getDataKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}dataKeys[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedModuleDefinition(
    var name: String? = null,
    var simpleCustomTypes: List<(ExpectedSimpleStructureDefinition.() -> Unit)>? = null,
    var complexCustomTypes: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var simpleValueObjects: List<(ExpectedSimpleStructureDefinition.() -> Unit)>? = null,
    var complexValueObjects: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var dataClasses: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var interfaces: List<(ExpectedInterfaceDefinition.() -> Unit)>? = null,
    var propertyKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
    var dataKeys: List<(ExpectedKeyDefinition.() -> Unit)>? = null,
    var enums: List<(ExpectedEnumDefinition.() -> Unit)>? = null,
    var implSubmodule: (ExpectedImplSubmoduleDefinition.() -> Unit)? = null,
)
fun diffModuleDefinition(given: ModuleDefinition, expectedInit: ExpectedModuleDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedModuleDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (diffModuleName(given.getName(), it) != "") { result.add(diffModuleName(given.getName(), it, "${path}name.")) }
    }

    expected.simpleCustomTypes?.let {
        if (given.getSimpleCustomTypes().size != it.size) { result.add("${path}simpleCustomTypes size ${given.getSimpleCustomTypes().size} != ${it.size}") }
        given.getSimpleCustomTypes().forEachIndexed { idx, entry -> if (diffSimpleStructureDefinition(entry, it[idx]) != "") { result.add(diffSimpleStructureDefinition(entry, it[idx], "${path}simpleCustomTypes[${idx}].")) } }
    }

    expected.complexCustomTypes?.let {
        if (given.getComplexCustomTypes().size != it.size) { result.add("${path}complexCustomTypes size ${given.getComplexCustomTypes().size} != ${it.size}") }
        given.getComplexCustomTypes().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}complexCustomTypes[${idx}].")) } }
    }

    expected.simpleValueObjects?.let {
        if (given.getSimpleValueObjects().size != it.size) { result.add("${path}simpleValueObjects size ${given.getSimpleValueObjects().size} != ${it.size}") }
        given.getSimpleValueObjects().forEachIndexed { idx, entry -> if (diffSimpleStructureDefinition(entry, it[idx]) != "") { result.add(diffSimpleStructureDefinition(entry, it[idx], "${path}simpleValueObjects[${idx}].")) } }
    }

    expected.complexValueObjects?.let {
        if (given.getComplexValueObjects().size != it.size) { result.add("${path}complexValueObjects size ${given.getComplexValueObjects().size} != ${it.size}") }
        given.getComplexValueObjects().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}complexValueObjects[${idx}].")) } }
    }

    expected.dataClasses?.let {
        if (given.getDataClasses().size != it.size) { result.add("${path}dataClasses size ${given.getDataClasses().size} != ${it.size}") }
        given.getDataClasses().forEachIndexed { idx, entry -> if (diffComplexStructureDefinition(entry, it[idx]) != "") { result.add(diffComplexStructureDefinition(entry, it[idx], "${path}dataClasses[${idx}].")) } }
    }

    expected.interfaces?.let {
        if (given.getInterfaces().size != it.size) { result.add("${path}interfaces size ${given.getInterfaces().size} != ${it.size}") }
        given.getInterfaces().forEachIndexed { idx, entry -> if (diffInterfaceDefinition(entry, it[idx]) != "") { result.add(diffInterfaceDefinition(entry, it[idx], "${path}interfaces[${idx}].")) } }
    }

    expected.propertyKeys?.let {
        if (given.getPropertyKeys().size != it.size) { result.add("${path}propertyKeys size ${given.getPropertyKeys().size} != ${it.size}") }
        given.getPropertyKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}propertyKeys[${idx}].")) } }
    }

    expected.dataKeys?.let {
        if (given.getDataKeys().size != it.size) { result.add("${path}dataKeys size ${given.getDataKeys().size} != ${it.size}") }
        given.getDataKeys().forEachIndexed { idx, entry -> if (diffKeyDefinition(entry, it[idx]) != "") { result.add(diffKeyDefinition(entry, it[idx], "${path}dataKeys[${idx}].")) } }
    }

    expected.enums?.let {
        if (given.getEnums().size != it.size) { result.add("${path}enums size ${given.getEnums().size} != ${it.size}") }
        given.getEnums().forEachIndexed { idx, entry -> if (diffEnumDefinition(entry, it[idx]) != "") { result.add(diffEnumDefinition(entry, it[idx], "${path}enums[${idx}].")) } }
    }

    expected.implSubmodule?.let {
        if (diffImplSubmoduleDefinition(given.getImplSubmodule(), it) != "") { result.add(diffImplSubmoduleDefinition(given.getImplSubmodule(), it, "${path}implSubmodule.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedTypeDefinition(
    var name: String? = null,
    var wrappers: List<TypeWrapper>? = null,
)
fun diffTypeDefinition(given: TypeDefinition, expectedInit: ExpectedTypeDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedTypeDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.wrappers?.let {
        if (given.getWrappers().size != it.size) { result.add("${path}wrappers size ${given.getWrappers().size} != ${it.size}") }
        given.getWrappers().forEachIndexed { idx, entry -> if (entry != it[idx]) { result.add("${path}wrappers[${idx}] ${entry} != ${it[idx]}") } }
    }

    return result.joinToString("\n")
}

data class ExpectedFieldDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
    var defaultValue: String? = null,
)
fun diffFieldDefinition(given: FieldDefinition, expectedInit: ExpectedFieldDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedFieldDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}") }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    expected.defaultValue?.let {
        if (given.getDefaultValue()!! != it) { result.add("${path}defaultValue ${given.getDefaultValue()!!} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedAttribute(
    var name: String? = null,
    var value: String? = null,
)
fun diffAttribute(given: Attribute, expectedInit: ExpectedAttribute.() -> Unit, path: String = ""): String {
    val expected = ExpectedAttribute().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.value?.let {
        if (given.getValue() != it) { result.add("${path}value ${given.getValue()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedSimpleStructureDefinition(
    var name: String? = null,
    var typeName: String? = null,
    var attributes: List<(ExpectedAttribute.() -> Unit)>? = null,
)
fun diffSimpleStructureDefinition(given: SimpleStructureDefinition, expectedInit: ExpectedSimpleStructureDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedSimpleStructureDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.typeName?.let {
        if (given.getTypeName() != it) { result.add("${path}typeName ${given.getTypeName()} != ${it}") }
    }

    expected.attributes?.let {
        if (given.getAttributes().size != it.size) { result.add("${path}attributes size ${given.getAttributes().size} != ${it.size}") }
        given.getAttributes().forEachIndexed { idx, entry -> if (diffAttribute(entry, it[idx]) != "") { result.add(diffAttribute(entry, it[idx], "${path}attributes[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedComplexStructureDefinition(
    var name: String? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun diffComplexStructureDefinition(given: ComplexStructureDefinition, expectedInit: ExpectedComplexStructureDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedComplexStructureDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.fields?.let {
        if (given.getFields().size != it.size) { result.add("${path}fields size ${given.getFields().size} != ${it.size}") }
        given.getFields().forEachIndexed { idx, entry -> if (diffFieldDefinition(entry, it[idx]) != "") { result.add(diffFieldDefinition(entry, it[idx], "${path}fields[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedInterfaceDefinition(
    var name: String? = null,
    var methods: List<(ExpectedMethodDefinition.() -> Unit)>? = null,
)
fun diffInterfaceDefinition(given: InterfaceDefinition, expectedInit: ExpectedInterfaceDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedInterfaceDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.methods?.let {
        if (given.getMethods().size != it.size) { result.add("${path}methods size ${given.getMethods().size} != ${it.size}") }
        given.getMethods().forEachIndexed { idx, entry -> if (diffMethodDefinition(entry, it[idx]) != "") { result.add(diffMethodDefinition(entry, it[idx], "${path}methods[${idx}].")) } }
    }

    return result.joinToString("\n")
}

data class ExpectedArgumentDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun diffArgumentDefinition(given: ArgumentDefinition, expectedInit: ExpectedArgumentDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedArgumentDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.type?.let {
        if (diffTypeDefinition(given.getType(), it) != "") { result.add(diffTypeDefinition(given.getType(), it, "${path}type.")) }
    }

    return result.joinToString("\n")
}

data class ExpectedExceptionDefinition(
    var name: String? = null,
)
fun diffExceptionDefinition(given: ExceptionDefinition, expectedInit: ExpectedExceptionDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedExceptionDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    return result.joinToString("\n")
}

data class ExpectedMethodDefinition(
    var name: String? = null,
    var returnType: (ExpectedTypeDefinition.() -> Unit)? = null,
    var args: List<(ExpectedArgumentDefinition.() -> Unit)>? = null,
    var throws: List<(ExpectedExceptionDefinition.() -> Unit)>? = null,
)
fun diffMethodDefinition(given: MethodDefinition, expectedInit: ExpectedMethodDefinition.() -> Unit, path: String = ""): String {
    val expected = ExpectedMethodDefinition().apply(expectedInit)
    val result: MutableList<String> = mutableListOf()

    expected.name?.let {
        if (given.getName() != it) { result.add("${path}name ${given.getName()} != ${it}") }
    }

    expected.returnType?.let {
        if (diffTypeDefinition(given.getReturnType(), it) != "") { result.add(diffTypeDefinition(given.getReturnType(), it, "${path}returnType.")) }
    }

    expected.args?.let {
        if (given.getArgs().size != it.size) { result.add("${path}args size ${given.getArgs().size} != ${it.size}") }
        given.getArgs().forEachIndexed { idx, entry -> if (diffArgumentDefinition(entry, it[idx]) != "") { result.add(diffArgumentDefinition(entry, it[idx], "${path}args[${idx}].")) } }
    }

    expected.throws?.let {
        if (given.getThrows().size != it.size) { result.add("${path}throws size ${given.getThrows().size} != ${it.size}") }
        given.getThrows().forEachIndexed { idx, entry -> if (diffExceptionDefinition(entry, it[idx]) != "") { result.add(diffExceptionDefinition(entry, it[idx], "${path}throws[${idx}].")) } }
    }

    return result.joinToString("\n")
}