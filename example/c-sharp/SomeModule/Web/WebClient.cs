// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using HttpClientModule.Api;
using SomeModule.Api;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Web {
    public class SomeInterfaceWebClient: SomeInterface {
        readonly HttpClient client;

        public SomeInterfaceWebClient(
            HttpClientFactory factory,
            SomeModuleWebClientConfig config
        ) {
            this.client = factory.Create(config.Value);
        }
        public void SomeEmptyMethod() {
            client.Post("/some/prefix/someInterface/someEmptyMethod", Optional<object>.Empty());
        }
        /// <exception cref="SomeException"/>
        /// <exception cref="Some2Exception"/>
        public void SomeCommand(SomeId id, int amount) {
            client.Post("/some/prefix/someInterface/someCommand", Optional<SomeInterfaceSomeCommandRequest>.Of(SomeInterfaceSomeCommandRequest.Create(id, amount)));
        }
        /// <exception cref="SomeException"/>
        public SomeClass SomeQuery(SomeQueryInput query) {
            return client.Post("/some/prefix/someInterface/someQuery", Optional<SomeInterfaceSomeQueryRequest>.Of(SomeInterfaceSomeQueryRequest.Create(query))).GetBody<SomeInterfaceSomeQueryResponse>().Get().GetValue();
        }
        public Optional<SomeClass> OptMethod(Optional<SomeId> optId) {
            return client.Post("/some/prefix/someInterface/optMethod", Optional<SomeInterfaceOptMethodRequest>.Of(SomeInterfaceOptMethodRequest.Create(optId))).GetBody<SomeInterfaceOptMethodResponse>().Get().GetValue();
        }
        public List<SomeId> MethodWithListOfSimpleVO(List<SomeId> list) {
            return client.Post("/some/prefix/someInterface/methodWithListOfSimpleVO", Optional<SomeInterfaceMethodWithListOfSimpleVORequest>.Of(SomeInterfaceMethodWithListOfSimpleVORequest.Create(list))).GetBody<SomeInterfaceMethodWithListOfSimpleVOResponse>().Get().GetValue();
        }
        public object MethodWithAny(object i) {
            return client.Post("/some/prefix/someInterface/methodWithAny", Optional<SomeInterfaceMethodWithAnyRequest>.Of(SomeInterfaceMethodWithAnyRequest.Create(i))).GetBody<SomeInterfaceMethodWithAnyResponse>().Get().GetValue();
        }
        public Optional<SomeId> MethodReturningOptSimpleVo() {
            return client.Post("/some/prefix/someInterface/methodReturningOptSimpleVo", Optional<object>.Empty()).GetBody<SomeInterfaceMethodReturningOptSimpleVoResponse>().Get().GetValue();
        }
    }

    public class SomeInterface2WebClient: SomeInterface2 {
        readonly HttpClient client;

        public SomeInterface2WebClient(
            HttpClientFactory factory,
            SomeModuleWebClientConfig config
        ) {
            this.client = factory.Create(config.Value);
        }
        public OtherClass ReferenceOtherClass(OtherClass other) {
            return client.Post("/some/prefix/someInterface2/referenceOtherClass", Optional<SomeInterface2ReferenceOtherClassRequest>.Of(SomeInterface2ReferenceOtherClassRequest.Create(other))).GetBody<SomeInterface2ReferenceOtherClassResponse>().Get().GetValue();
        }
        public LegacyType ReferenceLegacyType(LegacyType legacyType) {
            return client.Post("/some/prefix/someInterface2/referenceLegacyType", Optional<SomeInterface2ReferenceLegacyTypeRequest>.Of(SomeInterface2ReferenceLegacyTypeRequest.Create(legacyType))).GetBody<SomeInterface2ReferenceLegacyTypeResponse>().Get().GetValue();
        }
    }
}