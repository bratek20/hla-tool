import { OtherInterface } from "../Api/Interfaces"

export class OtherInterfaceLogic implements OtherInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    otherMethod(): void {
        // TODO
        return undefined
    }
}
