import { OtherInterface } from "../Api/Interfaces"

export class OtherInterfaceLogic implements OtherInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    otherMethod(): void {
        // TODO
        throw new Error("Method not implemented.")
    }
}
