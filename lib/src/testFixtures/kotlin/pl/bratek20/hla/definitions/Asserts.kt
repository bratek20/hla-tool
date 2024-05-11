package pl.bratek20.hla.definitions

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.definitions.api.*

data class ExpectedSimpleStructureDefinition(
    var name: String? = null,
    var type: String? = null,
)
fun assertSimpleValueObject(given: SimpleStructureDefinition, init: ExpectedSimpleStructureDefinition.() -> Unit) {
    val expected = ExpectedSimpleStructureDefinition().apply(init)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }
    if (expected.type != null) {
        assertThat(given.typeName).isEqualTo(expected.type)
    }
}

data class ExpectedTypeDefinition(
    var name: String? = null,
    var wrappers: List<TypeWrapper>? = null,
)
fun assertTypeDefinition(given: TypeDefinition, init: ExpectedTypeDefinition.() -> Unit) {
    val expected = ExpectedTypeDefinition().apply(init)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }

    expected.wrappers?.let {
        assertThat(given.wrappers).hasSize(it.size)
        given.wrappers.zip(it).forEach { (wrapper, expected) ->
            assertThat(wrapper.name).isEqualTo(expected.name)
        }
    }
}

data class ExpectedFieldDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertField(given: FieldDefinition, init: ExpectedFieldDefinition.() -> Unit) {
    val expected = ExpectedFieldDefinition().apply(init)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }
    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedComplexStructureDefinition(
    var name: String? = null,
    var fields: List<ExpectedFieldDefinition.() -> Unit>? = null,
)
fun assertComplexStructureDefinition(given: ComplexStructureDefinition, init: ExpectedComplexStructureDefinition.() -> Unit) {
    val expected = ExpectedComplexStructureDefinition().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.fields?.let {
        assertThat(given.fields).hasSize(it.size)
        given.fields.zip(it).forEach { (field, expected) ->
            assertField(field, expected)
        }
    }
}

data class ExpectedArg(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertArg(given: ArgumentDefinition, init: ExpectedArg.() -> Unit) {
    val expected = ExpectedArg().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedException(
    var name: String? = null,
)
fun assertException(given: ExceptionDefinition, init: ExpectedException.() -> Unit) {
    val expected = ExpectedException().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }
}

data class ExpectedMethod(
    var name: String? = null,
    var emptyReturnType: Boolean? = null,
    var returnType: (ExpectedTypeDefinition.() -> Unit)? = null,
    var args: List<ExpectedArg.() -> Unit>? = null,
    var throws: List<ExpectedException.() -> Unit>? = null,
)
fun assertMethod(given: MethodDefinition, init: ExpectedMethod.() -> Unit) {
    val expected = ExpectedMethod().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.emptyReturnType?.let {
        if (it) {
            assertThat(given.returnType).isNull()
        } else {
            assertThat(given.returnType).isNotNull()
        }
    }

    expected.returnType?.let {
        assertTypeDefinition(given.returnType!!, it)
    }

    expected.args?.let {
        assertThat(given.args).hasSize(it.size)
        given.args.zip(it).forEach { (arg, expected) ->
            assertArg(arg, expected)
        }
    }

    expected.throws?.let {
        assertThat(given.throws).hasSize(it.size)
        given.throws.zip(it).forEach { (exception, expected) ->
            assertException(exception, expected)
        }
    }
}

data class ExpectedInterface(
    var name: String? = null,
    var methods: List<ExpectedMethod.() -> Unit>? = null,
)
fun assertInterface(given: InterfaceDefinition, init: ExpectedInterface.() -> Unit) {
    val expected = ExpectedInterface().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.methods?.let {
        assertThat(given.methods).hasSize(it.size)
        given.methods.zip(it).forEach { (method, expected) ->
            assertMethod(method, expected)
        }
    }
}

data class ExpectedPropertyMapping(
    var key: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertPropertyMapping(given: PropertyMapping, init: ExpectedPropertyMapping.() -> Unit) {
    val expected = ExpectedPropertyMapping().apply(init)

    expected.key?.let {
        assertThat(given.key).isEqualTo(it)
    }

    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedEnum(
    var name: String? = null,
    var values: List<String>? = null,
)
fun assertEnum(given: EnumDefinition, init: ExpectedEnum.() -> Unit) {
    val expected = ExpectedEnum().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.values?.let {
        assertThat(given.values).hasSize(it.size)
        given.values.zip(it).forEach { (value, expected) ->
            assertThat(value).isEqualTo(expected)
        }
    }
}

data class ExpectedModule(
    var name: String? = null,
    var simpleValueObjects: List<ExpectedSimpleStructureDefinition.() -> Unit>? = null,
    var complexValueObjects: List<ExpectedComplexStructureDefinition.() -> Unit>? = null,
    var interfaces: List<ExpectedInterface.() -> Unit>? = null,
    var propertyValueObjects: List<ExpectedComplexStructureDefinition.() -> Unit>? = null,
    var propertyMappings: List<ExpectedPropertyMapping.() -> Unit>? = null,
    var enums: List<ExpectedEnum.() -> Unit>? = null,
)
fun assertModule(given: ModuleDefinition, init: ExpectedModule.() -> Unit) {
    val expected = ExpectedModule().apply(init)

    expected.name?.let {
        assertThat(given.name.value).isEqualTo(it)
    }

    expected.simpleValueObjects?.let {
        assertThat(given.simpleValueObjects).hasSameSizeAs(it)
        given.simpleValueObjects.zip(it).forEach { (simpleValueObject, expected) ->
            assertSimpleValueObject(simpleValueObject, expected)
        }
    }

    expected.complexValueObjects?.let {
        assertThat(given.complexValueObjects).hasSameSizeAs(it)
        given.complexValueObjects.zip(it).forEach { (complexValueObject, expected) ->
            assertComplexStructureDefinition(complexValueObject, expected)
        }
    }

    expected.interfaces?.let {
        assertThat(given.interfaces).hasSameSizeAs(it)
        given.interfaces.zip(it).forEach { (interf, expected) ->
            assertInterface(interf, expected)
        }
    }

    expected.propertyValueObjects?.let {
        assertThat(given.propertyValueObjects).hasSameSizeAs(it)
        given.propertyValueObjects.zip(it).forEach { (property, expected) ->
            assertComplexStructureDefinition(property, expected)
        }
    }

    expected.propertyMappings?.let {
        assertThat(given.propertyMappings).hasSameSizeAs(it)
        given.propertyMappings.zip(it).forEach { (mapping, expected) ->
            assertPropertyMapping(mapping, expected)
        }
    }

    expected.enums?.let {
        assertThat(given.enums).hasSameSizeAs(it)
        given.enums.zip(it).forEach { (enum, expected) ->
            assertEnum(enum, expected)
        }
    }
}

fun assertModules(given: List<ModuleDefinition>, init: List<ExpectedModule.() -> Unit>) {
    assertThat(given).hasSize(init.size)
    given.zip(init).forEach { (module, expected) ->
        assertModule(module, expected)
    }
}