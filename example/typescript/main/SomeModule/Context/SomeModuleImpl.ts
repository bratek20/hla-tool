namespace SomeModule.Api {
    export function someCommand(id: SomeId, amount: number, c: HandlerContext): void {
        new SomeInterfaceImpl(c).someCommand(id, amount)
    }

    export function someQuery(id: SomeId, c: HandlerContext): SomeClass {
        return new SomeInterfaceImpl(c).someQuery(id)
    }
}