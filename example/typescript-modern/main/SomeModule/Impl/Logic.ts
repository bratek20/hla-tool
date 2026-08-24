import { MockArg } from "../../ModuleOnlyForMocksArgs/Api/ValueObjects"
import { OtherInterface } from "../../OtherModule/Api/Interfaces"
import { OtherClass } from "../../OtherModule/Api/ValueObjects"
import { InterfaceForTracking, SomeEmptyInterface, SomeInterface, SomeInterface2, SomeInterface3, SomeInterfaceToTestMockArgsImport, SomeModuleDebugHandlers, SomeModuleHandlers } from "../Api/Interfaces"
import { LegacyType, SomeClass, SomeHandlerInput, SomeHandlerOutput, SomeId, SomeQueryInput } from "../Api/ValueObjects"

export class SomeEmptyInterfaceLogic implements SomeEmptyInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}
}

export class SomeInterfaceLogic implements SomeInterface {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someEmptyMethod(): void {
        // TODO
        throw new Error("Method not implemented.")
    }

    someCommand(id: SomeId, amount: number): void {
        // TODO
        throw new Error("Method not implemented.")
    }

    someQuery(query: SomeQueryInput): SomeClass {
        // TODO
        throw new Error("Method not implemented.")
    }

    optMethod(optId: Optional<SomeId>): Optional<SomeClass> {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodWithSimpleVO(id: SomeId): void {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodWithListOfSimpleVO(list: SomeId[]): SomeId[] {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodWithAny(i: any): any {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodWithBaseType(i: string): string {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodReturningOptSimpleVo(): Optional<SomeId> {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodReturningNumericType(): number {
        // TODO
        throw new Error("Method not implemented.")
    }

    methodWithOptionalMap(optMap: Optional<Map<string, string>>): Optional<Map<string, string>> {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class SomeInterface2Logic implements SomeInterface2 {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    referenceOtherClass(other: OtherClass): OtherClass {
        // TODO
        throw new Error("Method not implemented.")
    }

    referenceLegacyType(legacyType: LegacyType): LegacyType {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class SomeInterface3Logic implements SomeInterface3 {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface {
        // TODO
        throw new Error("Method not implemented.")
    }

    referenceOtherInterface(other: OtherInterface): OtherInterface {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class SomeInterfaceToTestMockArgsImportLogic implements SomeInterfaceToTestMockArgsImport {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someMethod(arg1: MockArg, arg2: MockArg): void {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class SomeModuleHandlersLogic implements SomeModuleHandlers {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someHandler(i: SomeHandlerInput): SomeHandlerOutput {
        // TODO
        throw new Error("Method not implemented.")
    }

    someHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class SomeModuleDebugHandlersLogic implements SomeModuleDebugHandlers {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    someDebugHandler(i: SomeHandlerInput): SomeHandlerOutput {
        // TODO
        throw new Error("Method not implemented.")
    }

    someDebugHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        // TODO
        throw new Error("Method not implemented.")
    }
}

export class InterfaceForTrackingLogic implements InterfaceForTracking {
    constructor(
        private readonly c: HandlerContext,
    ) {}

    getDimension(): TrackingDimension {
        // TODO
        throw new Error("Method not implemented.")
    }
}
