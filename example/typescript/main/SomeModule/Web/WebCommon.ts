// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Web {
    export class SomeInterfaceSomeCommandRequest {
        private readonly id = TODO
        private readonly amount = TODO
        getId(): SomeId {
            return new SomeId(id)
        }
        getAmount(): number {
            return amount
        }
        static create(id: SomeId, amount: number): SomeInterfaceSomeCommandRequest {
            instance = new SomeInterfaceSomeCommandRequest()
            instance.id = id.value
            instance.amount = amount
            return instance
        }
    }
    export class SomeInterfaceSomeQueryRequest {
        private readonly query = TODO
        getQuery(): SomeQueryInput {
            return query
        }
        static create(query: SomeQueryInput): SomeInterfaceSomeQueryRequest {
            instance = new SomeInterfaceSomeQueryRequest()
            instance.query = query
            return instance
        }
    }
    export class SomeInterfaceSomeQueryResponse {
        constructor(
            readonly value: SomeClass,
        ) {}
    }
    export class SomeInterfaceOptMethodRequest {
        private readonly optId = TODO
        getOptId(): Optional<SomeId> {
            return Optional.of(optId).map(it => new SomeId(it))
        }
        static create(optId: Optional<SomeId>): SomeInterfaceOptMethodRequest {
            instance = new SomeInterfaceOptMethodRequest()
            instance.optId = optId.map(it => it.value).orElse(undefined)
            return instance
        }
    }
    export class SomeInterfaceOptMethodResponse {
        constructor(
            readonly value: Optional<SomeClass>,
        ) {}
    }
    export class SomeInterface2ReferenceOtherClassRequest {
        private readonly other = TODO
        getOther(): OtherClass {
            return other
        }
        static create(other: OtherClass): SomeInterface2ReferenceOtherClassRequest {
            instance = new SomeInterface2ReferenceOtherClassRequest()
            instance.other = other
            return instance
        }
    }
    export class SomeInterface2ReferenceOtherClassResponse {
        constructor(
            readonly value: OtherClass,
        ) {}
    }
    export class SomeInterface2ReferenceLegacyTypeRequest {
        private readonly legacyType = TODO
        getLegacyType(): LegacyType {
            return legacyType
        }
        static create(legacyType: LegacyType): SomeInterface2ReferenceLegacyTypeRequest {
            instance = new SomeInterface2ReferenceLegacyTypeRequest()
            instance.legacyType = legacyType
            return instance
        }
    }
    export class SomeInterface2ReferenceLegacyTypeResponse {
        constructor(
            readonly value: LegacyType,
        ) {}
    }
}