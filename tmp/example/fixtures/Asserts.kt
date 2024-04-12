package pl.bratek20.example.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.example.api.*

data class ExpectedSomeClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun assertSomeClass(given: SomeClass, expectedInit: ExpectedSomeClass.() -> Unit) {
    val expected = ExpectedSomeClass().apply(expectedInit)

    if (expected.id != null) {
        assertThat(given.id).isEqualTo(expected.id)
    }
    if (expected.amount != null) {
        assertThat(given.amount).isEqualTo(expected.amount)
    }
}

data class ExpectedSomeClass2(
    var id: String? = null,
    var enabled: Boolean? = null,
)
fun assertSomeClass2(given: SomeClass2, expectedInit: ExpectedSomeClass2.() -> Unit) {
    val expected = ExpectedSomeClass2().apply(expectedInit)

    if (expected.id != null) {
        assertThat(given.id).isEqualTo(expected.id)
    }
    if (expected.enabled != null) {
        assertThat(given.enabled).isEqualTo(expected.enabled)
    }
}