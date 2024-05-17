package com.some.pkg.othermodule.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.some.pkg.othermodule.api.*

data class ExpectedOtherProperty(
    var id: String? = null,
    var name: String? = null,
)
fun assertOtherProperty(given: OtherProperty, expectedInit: ExpectedOtherProperty.() -> Unit) {
    val expected = ExpectedOtherProperty().apply(expectedInit)

    expected.id?.let {
        assertThat(given.getId().value).isEqualTo(it)
    }

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }
}

data class ExpectedOtherClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun assertOtherClass(given: OtherClass, expectedInit: ExpectedOtherClass.() -> Unit) {
    val expected = ExpectedOtherClass().apply(expectedInit)

    expected.id?.let {
        assertThat(given.id.value).isEqualTo(it)
    }

    expected.amount?.let {
        assertThat(given.amount).isEqualTo(it)
    }
}