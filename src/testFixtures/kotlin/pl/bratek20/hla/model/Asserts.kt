package pl.bratek20.hla.model

import org.assertj.core.api.Assertions.assertThat

data class ExpectedModule(
    var name: String? = null,
)
fun assertModule(given: HlaModule, init: ExpectedModule.() -> Unit) {
    val expected = ExpectedModule().apply(init)

    if (expected.name != null) {
        assertThat(given.name.value).isEqualTo(expected.name)
    }
}

fun assertModules(given: List<HlaModule>, init: List<ExpectedModule.() -> Unit>) {
    given.zip(init).forEach { (module, expected) ->
        assertModule(module, expected)
    }
}