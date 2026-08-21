import { WebRequest, WebResult } from "../Api/ValueObjects"
import { WebApiLogic } from "./Logic"

export const Api = {
    handleRequest(i: WebRequest, c: HandlerContext): WebResult {
        return new WebApiLogic(c).handleRequest(i)
    },
}
