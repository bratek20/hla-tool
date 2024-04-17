package pl.bratek20.example.api

interface SomeInterface {
    fun someCommand(id: String, amount: Int): Unit
    fun someQuery(id: String): SomeClass
}
