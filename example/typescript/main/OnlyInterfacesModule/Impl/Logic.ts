namespace OnlyInterfacesModule.Impl {
    export class OnlyInterfacesModuleInterfaceLogic implements OnlyInterfacesModuleInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        someMethod(): void {
            // TODO
            throw new Error("Method not implemented.")
        }
    }
}