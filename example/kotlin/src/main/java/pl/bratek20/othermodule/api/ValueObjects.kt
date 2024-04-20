package pl.bratek20.othermodule.api

data class OtherId(
    val value: String
)

data class OtherClass(
    val id: OtherId,
    val amount: Int,
)

