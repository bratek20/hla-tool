// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    export const config = new SomeModuleWebClientConfig(
        HttpClientConfig.create(
            EnvVars.Api.Get(new VariableName("someService.baseUrl2")),
            "someServerName2",
            Optional.of(HttpClientAuth.create(EnvVars.Api.Get(new VariableName("someService.auth2"))))
        )
    )
}

namespace SomeModule.Api {
    export function someEmptyMethod(c: HandlerContext): void {
        new Web.SomeInterfaceWebClient(Web.config, c).someEmptyMethod()
    }

    export function someCommand(id: SomeId, amount: number, c: HandlerContext): void {
        new Web.SomeInterfaceWebClient(Web.config, c).someCommand(id, amount)
    }

    export function someQuery(query: SomeQueryInput, c: HandlerContext): SomeClass {
        return new Web.SomeInterfaceWebClient(Web.config, c).someQuery(query)
    }

    export function optMethod(optId: Optional<SomeId>, c: HandlerContext): Optional<SomeClass> {
        return new Web.SomeInterfaceWebClient(Web.config, c).optMethod(optId)
    }

    export function referenceOtherClass(other: OtherClass, c: HandlerContext): OtherClass {
        return new Web.SomeInterface2WebClient(Web.config, c).referenceOtherClass(other)
    }

    export function referenceLegacyType(legacyType: LegacyType, c: HandlerContext): LegacyType {
        return new Web.SomeInterface2WebClient(Web.config, c).referenceLegacyType(legacyType)
    }
}