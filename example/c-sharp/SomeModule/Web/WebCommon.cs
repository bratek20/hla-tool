// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using B20.Ext;
using HttpClientModule.Api;
using SomeModule.Api;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Web {
    public class SomeModuleWebClientConfig {
        public HttpClientConfig Value { get; }

        public SomeModuleWebClientConfig(
            HttpClientConfig value
        ) {
            Value = value;
        }
    }

    public class SomeInterfaceSomeCommandRequest {
        readonly string id;
        readonly int amount;

        public SomeInterfaceSomeCommandRequest(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeInterfaceSomeCommandRequest Create(SomeId id, int amount) {
            return new SomeInterfaceSomeCommandRequest(id.Value, amount);
        }
    }

    public class SomeInterfaceSomeQueryRequest {
        readonly SomeQueryInput query;

        public SomeInterfaceSomeQueryRequest(
            SomeQueryInput query
        ) {
            this.query = query;
        }
        public SomeQueryInput GetQuery() {
            return query;
        }
        public static SomeInterfaceSomeQueryRequest Create(SomeQueryInput query) {
            return new SomeInterfaceSomeQueryRequest(query);
        }
    }

    public class SomeInterfaceSomeQueryResponse {
        readonly SomeClass value;

        public SomeInterfaceSomeQueryResponse(
            SomeClass value
        ) {
            this.value = value;
        }
        public SomeClass GetValue() {
            return value;
        }
    }

    public class SomeInterfaceOptMethodRequest {
        readonly string? optId;

        public SomeInterfaceOptMethodRequest(
            string? optId
        ) {
            this.optId = optId;
        }
        public Optional<SomeId> GetOptId() {
            return Optional<string>.Of(optId).Map( it => new SomeId(it) );
        }
        public static SomeInterfaceOptMethodRequest Create(Optional<SomeId> optId) {
            return new SomeInterfaceOptMethodRequest(optId.Map( it => it.Value ).OrElse(null));
        }
    }

    public class SomeInterfaceOptMethodResponse {
        readonly SomeClass? value;

        public SomeInterfaceOptMethodResponse(
            SomeClass? value
        ) {
            this.value = value;
        }
        public Optional<SomeClass> GetValue() {
            return Optional<SomeClass>.Of(value);
        }
    }

    public class SomeInterfaceMethodWithListOfSimpleVORequest {
        readonly List<string> list;

        public SomeInterfaceMethodWithListOfSimpleVORequest(
            List<string> list
        ) {
            this.list = list;
        }
        public List<SomeId> GetList() {
            return list.Select( it => new SomeId(it) );
        }
        public static SomeInterfaceMethodWithListOfSimpleVORequest Create(List<SomeId> list) {
            return new SomeInterfaceMethodWithListOfSimpleVORequest(list.Select( it => it.Value ));
        }
    }

    public class SomeInterfaceMethodWithListOfSimpleVOResponse {
        readonly List<string> value;

        public SomeInterfaceMethodWithListOfSimpleVOResponse(
            List<string> value
        ) {
            this.value = value;
        }
        public List<SomeId> GetValue() {
            return value.Select( it => new SomeId(it) );
        }
    }

    public class SomeInterfaceMethodWithAnyRequest {
        readonly object i;

        public SomeInterfaceMethodWithAnyRequest(
            object i
        ) {
            this.i = i;
        }
        public object GetI() {
            return i;
        }
        public static SomeInterfaceMethodWithAnyRequest Create(object i) {
            return new SomeInterfaceMethodWithAnyRequest(i);
        }
    }

    public class SomeInterfaceMethodWithAnyResponse {
        readonly object value;

        public SomeInterfaceMethodWithAnyResponse(
            object value
        ) {
            this.value = value;
        }
        public object GetValue() {
            return value;
        }
    }

    public class SomeInterfaceMethodReturningOptSimpleVoResponse {
        readonly string? value;

        public SomeInterfaceMethodReturningOptSimpleVoResponse(
            string? value
        ) {
            this.value = value;
        }
        public Optional<SomeId> GetValue() {
            return Optional<string>.Of(value).Map( it => new SomeId(it) );
        }
    }

    public class SomeInterface2ReferenceOtherClassRequest {
        readonly OtherClass other;

        public SomeInterface2ReferenceOtherClassRequest(
            OtherClass other
        ) {
            this.other = other;
        }
        public OtherClass GetOther() {
            return other;
        }
        public static SomeInterface2ReferenceOtherClassRequest Create(OtherClass other) {
            return new SomeInterface2ReferenceOtherClassRequest(other);
        }
    }

    public class SomeInterface2ReferenceOtherClassResponse {
        readonly OtherClass value;

        public SomeInterface2ReferenceOtherClassResponse(
            OtherClass value
        ) {
            this.value = value;
        }
        public OtherClass GetValue() {
            return value;
        }
    }

    public class SomeInterface2ReferenceLegacyTypeRequest {
        readonly LegacyType legacyType;

        public SomeInterface2ReferenceLegacyTypeRequest(
            LegacyType legacyType
        ) {
            this.legacyType = legacyType;
        }
        public LegacyType GetLegacyType() {
            return legacyType;
        }
        public static SomeInterface2ReferenceLegacyTypeRequest Create(LegacyType legacyType) {
            return new SomeInterface2ReferenceLegacyTypeRequest(legacyType);
        }
    }

    public class SomeInterface2ReferenceLegacyTypeResponse {
        readonly LegacyType value;

        public SomeInterface2ReferenceLegacyTypeResponse(
            LegacyType value
        ) {
            this.value = value;
        }
        public LegacyType GetValue() {
            return value;
        }
    }
}