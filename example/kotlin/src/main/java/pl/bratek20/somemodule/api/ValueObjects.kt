package pl.bratek20.somemodule.api

data class SomeId(
    val value: String
)

data class SomeClass(
    val id: SomeId,
    val amount: Int,
)

data class SomeClass2(
    val id: SomeId,
    val enabled: Boolean,
    val names: List<String>,
)

