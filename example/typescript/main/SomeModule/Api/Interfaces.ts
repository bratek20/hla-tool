// DO NOT EDIT! Autogenerated by HLA tool

interface SomeEmptyInterface {
}

interface SomeInterface {
    someEmptyMethod(): void

    /**
     * @throws { SomeException }
     * @throws { Some2Exception }
     */
    someCommand(id: SomeId, amount: number): void

    /**
     * @throws { SomeException }
     */
    someQuery(query: SomeQueryInput): SomeClass

    optMethod(optId: Optional<SomeId>): Optional<SomeClass>

    methodWithListOfSimpleVO(list: SomeId[]): SomeId[]

    methodWithAny(i: any): any
}

interface SomeInterface2 {
    referenceOtherClass(other: OtherClass): OtherClass

    referenceLegacyType(legacyType: LegacyType): LegacyType
}

interface SomeInterface3 {
    referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface

    referenceOtherInterface(other: OtherInterface): OtherInterface
}

interface SomeModuleHandlers {
    /**
     * @throws { SomeException }
     * @throws { Some2Exception }
     */
    someHandler(i: SomeHandlerInput): SomeHandlerOutput

    someHandler2(i: SomeHandlerInput): SomeHandlerOutput
}

interface SomeModuleDebugHandlers {
    someDebugHandler(i: SomeHandlerInput): SomeHandlerOutput

    someDebugHandler2(i: SomeHandlerInput): SomeHandlerOutput
}