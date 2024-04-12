package pl.bratek20.example.api

interface SomeInterface {
    fun someCommand(id: SomeId, amount: Int): void
    fun someQuery(id: SomeId): SomeClass
}
