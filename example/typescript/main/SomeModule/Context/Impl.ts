namespace SomeModule.Api {
    export function someCommand(id: SomeId, amount: number, c: HandlerContext): void {
        new Impl.SomeInterfaceLogic(c).someCommand(id, amount)
    }

    export function someQuery(id: SomeId, c: HandlerContext): SomeClass {
        return new Impl.SomeInterfaceLogic(c).someQuery(id)
    }

    export function optMethod(optId: Optional<SomeId>, c: HandlerContext): Optional<SomeClass> {
        return new Impl.SomeInterfaceLogic(c).optMethod(optId)
    }
}