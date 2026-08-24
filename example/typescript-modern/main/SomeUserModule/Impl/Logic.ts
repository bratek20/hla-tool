import { SomeUserInterface } from "../Api/Interfaces"

export class SomeUserInterfaceLogic implements SomeUserInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someMethod(): void {
        // TODO
        throw new Error("Method not implemented.")
    }
}
