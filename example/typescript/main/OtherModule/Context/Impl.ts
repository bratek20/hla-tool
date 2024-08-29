namespace OtherModule.Api {

    export function otherMethod(c: HandlerContext): void {
        new Impl.OtherInterfaceLogic(c).otherMethod()
    }
}