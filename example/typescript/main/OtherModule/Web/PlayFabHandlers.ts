// DO NOT EDIT! Autogenerated by HLA tool

namespace OtherModule.Web {
    Handlers.Api.Register({ dependencyName: DependencyName.OtherModule, handlers: [ { name: "OtherModule.otherMethod", handler: otherMethod } ] })

    export function otherMethod(rawRequest: any, c: HandlerContext): IOpResult {
        Api.otherMethod(c)
        return Utils.OK({})
    }
}