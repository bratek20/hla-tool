// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    Handlers.Api.RegisterModuleHandlers(DependencyName.SomeModule,
        ["SomeModule.someHandler", someHandler],
        ["SomeModule.someHandler2", someHandler2],
    );

    export function RegisterDebugHandlers() {
        Handlers.Api.RegisterModuleHandlers(DependencyName.SomeModule,
            ["SomeModule.Debug.someDebugHandler", someDebugHandler],
            ["SomeModule.Debug.someDebugHandler2", someDebugHandler2],
        );
    }

    function someHandler(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors());
        const response = Api.someHandler(request, c);
        return Utils.OK(response);
    }

    function someHandler2(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors());
        const response = Api.someHandler2(request, c);
        return Utils.OK(response);
    }

    function someDebugHandler(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors());
        const response = Api.someDebugHandler(request, c);
        return Utils.OK(response);
    }

    function someDebugHandler2(rawRequest: any, c: HandlerContext): IOpResult {
        const request = ObjectCreation.Api.FromInterface(SomeHandlerInput, rawRequest, ObjectCreationOptions.noErrors());
        const response = Api.someDebugHandler2(request, c);
        return Utils.OK(response);
    }

    // Error Codes Mapping
    Handlers.Api.AddExceptionMapper(SomeException, (e, c) => Utils.ECNR("EC1", e.message, c));
    Handlers.Api.AddExceptionMapper(Some2Exception, (e, c) => Utils.ECNR("EC2", e.message, c));
}
