interface SomeInterface {
    someCommand(id: SomeId, amount: Int, c: HandlerContext): void
    someQuery(id: SomeId, c: HandlerContext): SomeClass
}
