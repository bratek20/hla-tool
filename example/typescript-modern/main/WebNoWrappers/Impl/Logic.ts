import { WebApi } from "../Api/Interfaces"
import { WebRequest, WebResult } from "../Api/ValueObjects"

export class WebApiLogic implements WebApi {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    handleRequest(i: WebRequest): WebResult {
        // TODO
        return undefined
    }
}
