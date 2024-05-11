package com.some.pkg.somemodule.fixtures

import org.assertj.core.api.Assertions.assertThat

import com.some.pkg.othermodule.fixtures.*

import com.some.pkg.somemodule.api.*

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
        given.names.forEachIndexed { idx, entry -> assertThat(entry).isEqualTo(it[idx]) }
    }

    expected.ids?.let {
        assertThat(given.ids).hasSize(it.size)
        given.ids.forEachIndexed { idx, entry -> assertThat(entry.value).isEqualTo(it[idx]) }
    }
}

data class ExpectedSomeClass3(
    var class2Object: (ExpectedSomeClass2.() -> Unit)? = null,
    var class2List: List<(ExpectedSomeClass2.() -> Unit)>? = null,
    var someEnum: SomeEnum? = null,
)
fun assertSomeClass3(given: SomeClass3, expectedInit: ExpectedSomeClass3.() -> Unit) {
    val expected = ExpectedSomeClass3().apply(expectedInit)

    expected.class2Object?.let {
        assertSomeClass2(given.class2Object, it)
    }

    expected.class2List?.let {
        assertThat(given.class2List).hasSize(it.size)
        given.class2List.forEachIndexed { idx, entry -> assertSomeClass2(entry, it[idx]) }
    }

    expected.someEnum?.let {
        assertThat(given.someEnum).isEqualTo(it)
    }
}

data class ExpectedSomeClass4(
    var otherId: String? = null,
    var otherClass: (ExpectedOtherClass.() -> Unit)? = null,
    var otherIdList: List<String>? = null,
    var otherClassList: List<(ExpectedOtherClass.() -> Unit)>? = null,
)
fun assertSomeClass4(given: SomeClass4, expectedInit: ExpectedSomeClass4.() -> Unit) {
    val expected = ExpectedSomeClass4().apply(expectedInit)

    expected.otherId?.let {
        assertThat(given.otherId.value).isEqualTo(it)
    }

    expected.otherClass?.let {
        assertOtherClass(given.otherClass, it)
    }

    expected.otherIdList?.let {
        assertThat(given.otherIdList).hasSize(it.size)
        given.otherIdList.forEachIndexed { idx, entry -> assertThat(entry.value).isEqualTo(it[idx]) }
    }

    expected.otherClassList?.let {
        assertThat(given.otherClassList).hasSize(it.size)
        given.otherClassList.forEachIndexed { idx, entry -> assertOtherClass(entry, it[idx]) }
    }
}