import { OnlyInterfacesModuleInterfaceLogic } from "./Logic"

export const Api = {
    someMethod(c: HandlerContext): void {
        new OnlyInterfacesModuleInterfaceLogic(c).someMethod()
    },
}
