interface SomeInterface {
    someCommand(id: SomeId, amount: number, c: HandlerContext): void
    someQuery(id: SomeId, c: HandlerContext): SomeClass
}

