// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    Handlers.Api.Register({ dependencyName: DependencyName.SomeModule, handlers: [ { name: "SomeModule.someHandler", handler: someHandler }, { name: "MyHandlerName", handler: someHandler2 } ], featureFlag: FeatureName.SomeModule })

    export function RegisterDebugHandlers() {
        Handlers.Api.Register({ dependencyName: DependencyName.SomeModule, handlers: [ { name: "SomeModule.Debug.someDebugHandler", handler: someDebugHandler }, { name: "MyDebugHandlerName", handler: someDebugHandler2 } ] })
    }

    export function someHandler(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors())
        const response = Api.someHandler(request, c)
        return Utils.OK(response)
    }

    export function someHandler2(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors())
        const response = Api.someHandler2(request, c)
        return Utils.OK(response)
    }

    export function someDebugHandler(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors())
        const response = Api.someDebugHandler(request, c)
        return Utils.OK(response)
    }

    export function someDebugHandler2(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors())
        const response = Api.someDebugHandler2(request, c)
        return Utils.OK(response)
    }

    // Error Codes Mapping
    Handlers.Api.AddExceptionMapper(SomeException, (e, c) => Utils.ECNR("EC1", e.message, c))

    Handlers.Api.AddExceptionMapper(Some2Exception, (e, c) => Utils.ECNR("EC2", e.message, c))
}