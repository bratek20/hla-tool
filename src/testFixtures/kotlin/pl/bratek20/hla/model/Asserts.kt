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

data class ExpectedModule(
    var name: String? = null,
    var simpleValueObjects: List<ExpectedSimpleValueObject.() -> Unit>? = null,
    var complexValueObjects: List<ExpectedComplexValueObject.() -> Unit>? = null,
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
}

fun assertModules(given: List<HlaModule>, init: List<ExpectedModule.() -> Unit>) {
    assertThat(given).hasSize(init.size)
    given.zip(init).forEach { (module, expected) ->
        assertModule(module, expected)
    }
}