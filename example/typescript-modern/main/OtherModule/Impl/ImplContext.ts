import { OtherInterfaceLogic } from "./Logic"

export const Api = {
    otherMethod(c: HandlerContext): void {
        new OtherInterfaceLogic(c).otherMethod()
    },
}
