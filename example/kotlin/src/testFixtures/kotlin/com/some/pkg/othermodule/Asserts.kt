package com.some.pkg.othermodule

import com.some.pkg.othermodule.api.OtherClass
import org.assertj.core.api.Assertions.assertThat

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