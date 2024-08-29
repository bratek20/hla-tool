namespace OtherModule.Api {

    export function otherMethod(c: HandlerContext): void {
        new Impl.OtherInterfaceLogic(c).otherMethod()
    }

    export function otherHandler(i: OtherHandlerInput, c: HandlerContext): OtherHandlerOutput {
        return new Impl.OtherModuleHandlersLogic(c).otherHandler(i)
    }
}