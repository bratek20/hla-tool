namespace SomeModule.Impl {
    export class SomeEmptyInterfaceLogic implements SomeEmptyInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}
    }

    export class SomeInterfaceLogic implements SomeInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        someCommand(id: SomeId, amount: number): void {
            // TODO
            return undefined
        }

        someQuery(id: SomeId): SomeClass {
            // TODO
            return undefined
        }

        optMethod(optId: Optional<SomeId>): Optional<SomeClass> {
            // TODO
            return undefined
        }
    }

    export class SomeInterface2Logic implements SomeInterface2 {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface {
            // TODO
            return undefined
        }

        referenceOtherInterface(other: OtherInterface): OtherInterface {
            // TODO
            return undefined
        }

        referenceLegacyType(legacyType: LegacyType): LegacyType {
            // TODO
            return undefined
        }
    }
}