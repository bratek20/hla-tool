import { OnlyInterfacesModuleInterface } from "../Api/Interfaces"

export class OnlyInterfacesModuleInterfaceLogic implements OnlyInterfacesModuleInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someMethod(): void {
        // TODO
        return undefined
    }
}
