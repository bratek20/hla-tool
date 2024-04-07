package pl.bratek20.somemodule

interface SomeInterface {
    fun someCommand(id: SomeId, amount: Int): Unit
    fun someQuery(id: SomeId): SomeClass
}