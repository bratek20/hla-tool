// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    export class SomeInterfaceSomeCommandRequest {
        private id = STRING
        private amount = NUMBER
        getId(): SomeId {
            return new SomeId(id)
        }
        getAmount(): number {
            return amount
        }
        static create(id: SomeId, amount: number): SomeInterfaceSomeCommandRequest {
            const instance = new SomeInterfaceSomeCommandRequest()
            instance.id = id.value
            instance.amount = amount
            return instance
        }
    }
    export class SomeInterfaceSomeQueryRequest {
        private query = new SomeQueryInput
        getQuery(): SomeQueryInput {
            return query
        }
        static create(query: SomeQueryInput): SomeInterfaceSomeQueryRequest {
            const instance = new SomeInterfaceSomeQueryRequest()
            instance.query = query
            return instance
        }
    }
    export class SomeInterfaceSomeQueryResponse {
        private value = new SomeClass
        getValue(): SomeClass {
            return value
        }
    }
    export class SomeInterfaceOptMethodRequest {
        private optId = STRING
        getOptId(): Optional<SomeId> {
            return Optional.of(optId).map(it => new SomeId(it))
        }
        static create(optId: Optional<SomeId>): SomeInterfaceOptMethodRequest {
            const instance = new SomeInterfaceOptMethodRequest()
            instance.optId = optId.map(it => it.value).orElse(undefined)
            return instance
        }
    }
    export class SomeInterfaceOptMethodResponse {
        private value = new SomeClass
        getValue(): Optional<SomeClass> {
            return Optional.of(value)
        }
    }
    export class SomeInterface2ReferenceOtherClassRequest {
        private other = new OtherClass
        getOther(): OtherClass {
            return other
        }
        static create(other: OtherClass): SomeInterface2ReferenceOtherClassRequest {
            const instance = new SomeInterface2ReferenceOtherClassRequest()
            instance.other = other
            return instance
        }
    }
    export class SomeInterface2ReferenceOtherClassResponse {
        private value = new OtherClass
        getValue(): OtherClass {
            return value
        }
    }
    export class SomeInterface2ReferenceLegacyTypeRequest {
        private legacyType = new LegacyType
        getLegacyType(): LegacyType {
            return legacyType
        }
        static create(legacyType: LegacyType): SomeInterface2ReferenceLegacyTypeRequest {
            const instance = new SomeInterface2ReferenceLegacyTypeRequest()
            instance.legacyType = legacyType
            return instance
        }
    }
    export class SomeInterface2ReferenceLegacyTypeResponse {
        private value = new LegacyType
        getValue(): LegacyType {
            return value
        }
    }
}