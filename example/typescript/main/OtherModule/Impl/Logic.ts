namespace OtherModule.Impl {
    export class OtherInterfaceLogic implements OtherInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        otherMethod(): void {
            // TODO
            return undefined
        }
    }

    export class OtherModuleHandlersLogic implements OtherModuleHandlers {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        otherHandler(i: OtherHandlerInput): OtherHandlerOutput {
            // TODO
            return undefined
        }
    }
}