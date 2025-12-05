namespace SomeUserModule.Api {

    export function someMethod(c: HandlerContext): void {
        new Impl.SomeUserInterfaceLogic(c).someMethod()
    }
}