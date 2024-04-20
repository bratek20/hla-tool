package pl.bratek20.somemodule.fixtures

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.somemodule.api.*

data class ExpectedSomeClass(
    var id: String? = null,
    var amount: Int? = null,
)
fun assertSomeClass(given: SomeClass, expectedInit: ExpectedSomeClass.() -> Unit) {
    val expected = ExpectedSomeClass().apply(expectedInit)

    expected.id?.let {
        assertThat(given.id.value).isEqualTo(it)
    }

    expected.amount?.let {
        assertThat(given.amount).isEqualTo(it)
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

    expected.id?.let {
        assertThat(given.id.value).isEqualTo(it)
    }

    expected.enabled?.let {
        assertThat(given.enabled).isEqualTo(it)
    }

    expected.names?.let {
        assertThat(given.names).hasSize(it.size)
        given.names.forEachIndexed { index, entry -> assertThat(entry).isEqualTo(it[index]) }
    }

    expected.ids?.let {
        assertThat(given.ids).hasSize(it.size)
        given.ids.forEachIndexed { index, entry -> assertThat(entry.value).isEqualTo(it[index]) }
    }
}

data class ExpectedSomeClass3(
    var class2Object: (ExpectedSomeClass2.() -> Unit)? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
)
fun assertSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit) {
    val expected = ExpectedSomeClass3().apply(expectedInit)

    expected.class2Object?.let {
        assertSomeClass2(given.class2Object, it)
    }

    expected.class2List?.let {
        assertThat(given.class2List).hasSize(it.size)
        given.class2List.forEachIndexed { index, entry -> assertSomeClass2(entry, it[index]) }
    }
}

