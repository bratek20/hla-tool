namespace OnlyInterfacesModule.Api {

    export function someMethod(c: HandlerContext): void {
        new Impl.OnlyInterfacesModuleInterfaceLogic(c).someMethod()
    }
}