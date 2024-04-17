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
    val ids: List<SomeId>,
)

data class SomeClass3(
    val class2Object: SomeClass2,
    val class2List: List<SomeClass2>,
)

