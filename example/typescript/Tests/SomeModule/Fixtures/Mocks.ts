// DO NOT EDIT! Autogenerated by HLA tool

class SomeEmptyInterfaceMock implements SomeEmptyInterface {
}

class SomeInterfaceMock implements SomeInterface {
    someEmptyMethod(): void {
    }
    someCommand(id: SomeId, amount: number): void {
    }
    someQuery(query: SomeQueryInput): SomeClass {
        return someClass(undefined)
    }
    optMethod(optId: Optional<SomeId>): Optional<SomeClass> {
        return Optional.of(undefined).map(it => someClass(it))
    }
    methodWithListOfSimpleVO(list: SomeId[]): SomeId[] {
        return [].map(it => new SomeId(it))
    }
    methodWithAny(i: any): any {
        return undefined
    }
    methodReturningOptSimpleVo(): Optional<SomeId> {
        return Optional.of(undefined).map(it => new SomeId(it))
    }
}

class SomeInterface2Mock implements SomeInterface2 {
    referenceOtherClass(other: OtherClass): OtherClass {
        return OtherModule.Builder.otherClass(undefined)
    }
    referenceLegacyType(legacyType: LegacyType): LegacyType {
        return TODO()
    }
}

class SomeInterface3Mock implements SomeInterface3 {
    referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface {
        return TODO()
    }
    referenceOtherInterface(other: OtherInterface): OtherInterface {
        return TODO()
    }
}

class SomeModuleHandlersMock implements SomeModuleHandlers {
    someHandler(i: SomeHandlerInput): SomeHandlerOutput {
        return someHandlerOutput(undefined)
    }
    someHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        return someHandlerOutput(undefined)
    }
}

class SomeModuleDebugHandlersMock implements SomeModuleDebugHandlers {
    someDebugHandler(i: SomeHandlerInput): SomeHandlerOutput {
        return someHandlerOutput(undefined)
    }
    someDebugHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        return someHandlerOutput(undefined)
    }
}

namespace SomeModule.Mocks {
    export function createSomeEmptyInterfaceMock(): SomeEmptyInterfaceMock {
        return new SomeEmptyInterfaceMock()
    }

    export function setupSomeEmptyInterface(): SomeEmptyInterfaceMock {
        const mock = SomeModule.Mocks.createSomeEmptyInterfaceMock()
        return mock
    }

    export function createSomeInterfaceMock(): SomeInterfaceMock {
        return new SomeInterfaceMock()
    }

    export function setupSomeInterface(): SomeInterfaceMock {
        const mock = SomeModule.Mocks.createSomeInterfaceMock()
        SomeModule.Api.someEmptyMethod = CreateMock(SomeModule.Api.someEmptyMethod, () => { mock.someEmptyMethod() })
        SomeModule.Api.someCommand = CreateMock(SomeModule.Api.someCommand, (id: SomeId, amount: number) => { mock.someCommand(id, amount) })
        SomeModule.Api.someQuery = CreateMock(SomeModule.Api.someQuery, (query: SomeQueryInput) => { return mock.someQuery(query) })
        SomeModule.Api.optMethod = CreateMock(SomeModule.Api.optMethod, (optId: Optional<SomeId>) => { return mock.optMethod(optId) })
        SomeModule.Api.methodWithListOfSimpleVO = CreateMock(SomeModule.Api.methodWithListOfSimpleVO, (list: SomeId[]) => { return mock.methodWithListOfSimpleVO(list) })
        SomeModule.Api.methodWithAny = CreateMock(SomeModule.Api.methodWithAny, (i: any) => { return mock.methodWithAny(i) })
        SomeModule.Api.methodReturningOptSimpleVo = CreateMock(SomeModule.Api.methodReturningOptSimpleVo, () => { return mock.methodReturningOptSimpleVo() })
        return mock
    }

    export function createSomeInterface2Mock(): SomeInterface2Mock {
        return new SomeInterface2Mock()
    }

    export function setupSomeInterface2(): SomeInterface2Mock {
        const mock = SomeModule.Mocks.createSomeInterface2Mock()
        SomeModule.Api.referenceOtherClass = CreateMock(SomeModule.Api.referenceOtherClass, (other: OtherClass) => { return mock.referenceOtherClass(other) })
        SomeModule.Api.referenceLegacyType = CreateMock(SomeModule.Api.referenceLegacyType, (legacyType: LegacyType) => { return mock.referenceLegacyType(legacyType) })
        return mock
    }

    export function createSomeInterface3Mock(): SomeInterface3Mock {
        return new SomeInterface3Mock()
    }

    export function setupSomeInterface3(): SomeInterface3Mock {
        const mock = SomeModule.Mocks.createSomeInterface3Mock()
        SomeModule.Api.referenceInterface = CreateMock(SomeModule.Api.referenceInterface, (empty: SomeEmptyInterface) => { return mock.referenceInterface(empty) })
        SomeModule.Api.referenceOtherInterface = CreateMock(SomeModule.Api.referenceOtherInterface, (other: OtherInterface) => { return mock.referenceOtherInterface(other) })
        return mock
    }

    export function createSomeModuleHandlersMock(): SomeModuleHandlersMock {
        return new SomeModuleHandlersMock()
    }

    export function setupSomeModuleHandlers(): SomeModuleHandlersMock {
        const mock = SomeModule.Mocks.createSomeModuleHandlersMock()
        SomeModule.Api.someHandler = CreateMock(SomeModule.Api.someHandler, (i: SomeHandlerInput) => { return mock.someHandler(i) })
        SomeModule.Api.someHandler2 = CreateMock(SomeModule.Api.someHandler2, (i: SomeHandlerInput) => { return mock.someHandler2(i) })
        return mock
    }

    export function createSomeModuleDebugHandlersMock(): SomeModuleDebugHandlersMock {
        return new SomeModuleDebugHandlersMock()
    }

    export function setupSomeModuleDebugHandlers(): SomeModuleDebugHandlersMock {
        const mock = SomeModule.Mocks.createSomeModuleDebugHandlersMock()
        SomeModule.Api.someDebugHandler = CreateMock(SomeModule.Api.someDebugHandler, (i: SomeHandlerInput) => { return mock.someDebugHandler(i) })
        SomeModule.Api.someDebugHandler2 = CreateMock(SomeModule.Api.someDebugHandler2, (i: SomeHandlerInput) => { return mock.someDebugHandler2(i) })
        return mock
    }
}