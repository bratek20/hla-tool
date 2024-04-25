package pl.bratek20.hla.model

import org.assertj.core.api.Assertions.assertThat

data class ExpectedSimpleValueObject(
    var name: String? = null,
    var type: String? = null,
)
fun assertSimpleValueObject(given: SimpleValueObject, init: ExpectedSimpleValueObject.() -> Unit) {
    val expected = ExpectedSimpleValueObject().apply(init)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }
    if (expected.type != null) {
        assertThat(given.typeName).isEqualTo(expected.type)
    }
}

data class ExpectedType(
    var name: String? = null,
    var wrappers: List<TypeWrapper>? = null,
)
fun assertType(given: Type, init: ExpectedType.() -> Unit) {
    val expected = ExpectedType().apply(init)

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

data class ExpectedField(
    var name: String? = null,
    var type: (ExpectedType.() -> Unit)? = null,
)
fun assertField(given: Field, init: ExpectedField.() -> Unit) {
    val expected = ExpectedField().apply(init)

    if (expected.name != null) {
        assertThat(given.name).isEqualTo(expected.name)
    }
    expected.type?.let {
        assertType(given.type, it)
    }
}

data class ExpectedComplexValueObject(
    var name: String? = null,
    var fields: List<ExpectedField.() -> Unit>? = null,
)
fun assertComplexValueObject(given: ComplexValueObject, init: ExpectedComplexValueObject.() -> Unit) {
    val expected = ExpectedComplexValueObject().apply(init)

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
    var type: (ExpectedType.() -> Unit)? = null,
)
fun assertArg(given: Argument, init: ExpectedArg.() -> Unit) {
    val expected = ExpectedArg().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.type?.let {
        assertType(given.type, it)
    }
}

data class ExpectedMethod(
    var name: String? = null,
    var emptyReturnType: Boolean? = null,
    var returnType: (ExpectedType.() -> Unit)? = null,
    var args: List<ExpectedArg.() -> Unit>? = null,
)
fun assertMethod(given: Method, init: ExpectedMethod.() -> Unit) {
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
        assertType(given.returnType!!, it)
    }

    expected.args?.let {
        assertThat(given.args).hasSize(it.size)
        given.args.zip(it).forEach { (arg, expected) ->
            assertArg(arg, expected)
        }
    }
}

data class ExpectedInterface(
    var name: String? = null,
    var methods: List<ExpectedMethod.() -> Unit>? = null,
)
fun assertInterface(given: Interface, init: ExpectedInterface.() -> Unit) {
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

data class ExpectedProperty(
    var name: String? = null,
    var isList: Boolean? = null,
    var type: (ExpectedComplexValueObject.() -> Unit)? = null,
)
fun assertProperty(given: Property, init: ExpectedProperty.() -> Unit) {
    val expected = ExpectedProperty().apply(init)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.isList?.let {
        assertThat(given.isList).isEqualTo(it)
    }

    expected.type?.let {
        assertComplexValueObject(given.type, it)
    }
}

data class ExpectedModule(
    var name: String? = null,
    var simpleValueObjects: List<ExpectedSimpleValueObject.() -> Unit>? = null,
    var complexValueObjects: List<ExpectedComplexValueObject.() -> Unit>? = null,
    var interfaces: List<ExpectedInterface.() -> Unit>? = null,
    var properties: List<ExpectedProperty.() -> Unit>? = null,
)
fun assertModule(given: HlaModule, init: ExpectedModule.() -> Unit) {
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
            assertComplexValueObject(complexValueObject, expected)
        }
    }

    expected.interfaces?.let {
        assertThat(given.interfaces).hasSameSizeAs(it)
        given.interfaces.zip(it).forEach { (interf, expected) ->
            assertInterface(interf, expected)
        }
    }

    expected.properties?.let {
        assertThat(given.properties).hasSameSizeAs(it)
        given.properties.zip(it).forEach { (property, expected) ->
            assertProperty(property, expected)
        }
    }
}

fun assertModules(given: List<HlaModule>, init: List<ExpectedModule.() -> Unit>) {
    assertThat(given).hasSize(init.size)
    given.zip(init).forEach { (module, expected) ->
        assertModule(module, expected)
    }
}