namespace WebNoWrappers.Impl {
    export class WebApiLogic implements WebApi {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        handleRequest(i: WebRequest): WebResult {
            // TODO
            throw new Error("Method not implemented.")
        }
    }
}