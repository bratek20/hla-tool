// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    export class SomeInterfaceWebClient implements SomeInterface {
        constructor(
            config: SomeModuleWebClientConfig,
            c: HandlerContext
        ) {
            this.client = HttpClient.Api.create(config.value, c)
        }
        private readonly client: HttpClient

        someEmptyMethod(): void {
            client.post("/someInterface/someEmptyMethod", Optional.empty())
        }

        someCommand(id: SomeId, amount: number): void {
            client.post("/someInterface/someCommand", Optional.of(SomeInterfaceSomeCommandRequest.create(id, amount)))
        }

        someQuery(query: SomeQueryInput): SomeClass {
            return client.post("/someInterface/someQuery", Optional.of(SomeInterfaceSomeQueryRequest.create(query))).getBody(SomeInterfaceSomeQueryResponse).get().value
        }

        optMethod(optId: Optional<SomeId>): Optional<SomeClass> {
            return client.post("/someInterface/optMethod", Optional.of(SomeInterfaceOptMethodRequest.create(optId))).getBody(SomeInterfaceOptMethodResponse).get().value
        }
    }
}



