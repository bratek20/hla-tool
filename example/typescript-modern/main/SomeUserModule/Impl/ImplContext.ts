import { SomeUserInterfaceLogic } from "./Logic"

export const Api = {
    someMethod(c: HandlerContext): void {
        new SomeUserInterfaceLogic(c).someMethod()
    },
}
