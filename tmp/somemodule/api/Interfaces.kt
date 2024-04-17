package pl.bratek20.somemodule.api

interface SomeInterface {
    fun someCommand(id: SomeId, amount: Int): Unit
    fun someQuery(id: SomeId): SomeClass
}
