package pl.bratek20.somemodule.api

interface SomeInterface {
    @Throws(
        SomeException::class,
        Some2Exception::class
    )
    fun someCommand(id: SomeId, amount: Int): Unit

    @Throws(
        SomeException::class
    )
    fun someQuery(id: SomeId): SomeClass
}