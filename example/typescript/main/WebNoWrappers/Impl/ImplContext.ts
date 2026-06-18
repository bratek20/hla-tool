namespace WebNoWrappers.Api {

    export function handleRequest(i: WebRequest, c: HandlerContext): WebResult {
        return new Impl.WebApiLogic(c).handleRequest(i)
    }
}