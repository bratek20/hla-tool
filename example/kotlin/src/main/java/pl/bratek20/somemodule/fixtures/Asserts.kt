package pl.bratek20.somemodule.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.somemodule.api.*

data class ExpectedSomeClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun assertSomeClass(given: SomeClass, expectedInit: ExpectedSomeClass.() -> Unit) {
    val expected = ExpectedSomeClass().apply(expectedInit)

    if (expected.id != null) {
        assertThat(given.id.value).isEqualTo(expected.id)
    }
    if (expected.amount != null) {
        assertThat(given.amount).isEqualTo(expected.amount)
    }
}

data class ExpectedSomeClass2(
    var id: String? = null,
    var enabled: Boolean? = null,
    var names: List<String>? = null,
    var ids: List<String>? = null,
)
fun assertSomeClass2(given: SomeClass2, expectedInit: ExpectedSomeClass2.() -> Unit) {
    val expected = ExpectedSomeClass2().apply(expectedInit)

    if (expected.id != null) {
        assertThat(given.id.value).isEqualTo(expected.id)
    }
    if (expected.enabled != null) {
        assertThat(given.enabled).isEqualTo(expected.enabled)
    }
    if (expected.names != null) {
        assertThat(given.names).isEqualTo(expected.names)
    }
    if (expected.ids != null) {
        assertThat(given.ids.map { it.value }).isEqualTo(expected.ids)
    }
}

data class ExpectedSomeClass3(
    var class2Object: (ExpectedSomeClass2.() -> Unit)? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
)
fun assertSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit) {
    val expected = ExpectedSomeClass3().apply(expectedInit)

    if (expected.class2Object != null) {
        assertSomeClass2(given.class2Object, expected.class2Object!!)
    }

    if (expected.class2List != null) {
        assertThat(given.class2List).hasSize(expected.class2List!!.size)
        expected.class2List!!.forEachIndexed { index, expectedClass2 ->
            assertSomeClass2(given.class2List[index], expectedClass2)
        }
    }
}


