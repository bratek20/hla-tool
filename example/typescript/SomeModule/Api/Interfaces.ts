interface SomeInterface {
    /**
     * @throws { SomeException }
     * @throws { Some2Exception }
     */
    someCommand(id: SomeId, amount: number, c: HandlerContext): void

    /**
     * @throws { SomeException }
     */
    someQuery(id: SomeId, c: HandlerContext): SomeClass
}