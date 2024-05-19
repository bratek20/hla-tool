namespace SomeModule.Impl {
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
    }
}