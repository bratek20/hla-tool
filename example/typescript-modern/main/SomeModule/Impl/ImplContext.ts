import { MockArg } from "../../ModuleOnlyForMocksArgs/Api/ValueObjects"
import { OtherInterface } from "../../OtherModule/Api/Interfaces"
import { OtherClass } from "../../OtherModule/Api/ValueObjects"
import { SomeEmptyInterface } from "../Api/Interfaces"
import { LegacyType, SomeClass, SomeHandlerInput, SomeHandlerOutput, SomeId, SomeQueryInput } from "../Api/ValueObjects"
import { InterfaceForTrackingLogic, SomeInterface2Logic, SomeInterface3Logic, SomeInterfaceLogic, SomeInterfaceToTestMockArgsImportLogic, SomeModuleDebugHandlersLogic, SomeModuleHandlersLogic } from "./Logic"

export const Api = {
    someEmptyMethod(c: HandlerContext): void {
        new SomeInterfaceLogic(c).someEmptyMethod()
    },
    someCommand(id: SomeId, amount: number, c: HandlerContext): void {
        new SomeInterfaceLogic(c).someCommand(id, amount)
    },
    someQuery(query: SomeQueryInput, c: HandlerContext): SomeClass {
        return new SomeInterfaceLogic(c).someQuery(query)
    },
    optMethod(optId: Optional<SomeId>, c: HandlerContext): Optional<SomeClass> {
        return new SomeInterfaceLogic(c).optMethod(optId)
    },
    methodWithSimpleVO(id: SomeId, c: HandlerContext): void {
        new SomeInterfaceLogic(c).methodWithSimpleVO(id)
    },
    methodWithListOfSimpleVO(list: SomeId[], c: HandlerContext): SomeId[] {
        return new SomeInterfaceLogic(c).methodWithListOfSimpleVO(list)
    },
    methodWithAny(i: any, c: HandlerContext): any {
        return new SomeInterfaceLogic(c).methodWithAny(i)
    },
    methodWithBaseType(i: string, c: HandlerContext): string {
        return new SomeInterfaceLogic(c).methodWithBaseType(i)
    },
    methodReturningOptSimpleVo(c: HandlerContext): Optional<SomeId> {
        return new SomeInterfaceLogic(c).methodReturningOptSimpleVo()
    },
    methodReturningNumericType(c: HandlerContext): number {
        return new SomeInterfaceLogic(c).methodReturningNumericType()
    },
    methodWithOptionalMap(optMap: Optional<Map<string, string>>, c: HandlerContext): Optional<Map<string, string>> {
        return new SomeInterfaceLogic(c).methodWithOptionalMap(optMap)
    },
    referenceOtherClass(other: OtherClass, c: HandlerContext): OtherClass {
        return new SomeInterface2Logic(c).referenceOtherClass(other)
    },
    referenceLegacyType(legacyType: LegacyType, c: HandlerContext): LegacyType {
        return new SomeInterface2Logic(c).referenceLegacyType(legacyType)
    },
    referenceInterface(empty: SomeEmptyInterface, c: HandlerContext): SomeEmptyInterface {
        return new SomeInterface3Logic(c).referenceInterface(empty)
    },
    referenceOtherInterface(other: OtherInterface, c: HandlerContext): OtherInterface {
        return new SomeInterface3Logic(c).referenceOtherInterface(other)
    },
    someMethod(arg1: MockArg, arg2: MockArg, c: HandlerContext): void {
        new SomeInterfaceToTestMockArgsImportLogic(c).someMethod(arg1, arg2)
    },
    someHandler(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new SomeModuleHandlersLogic(c).someHandler(i)
    },
    someHandler2(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new SomeModuleHandlersLogic(c).someHandler2(i)
    },
    someDebugHandler(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new SomeModuleDebugHandlersLogic(c).someDebugHandler(i)
    },
    someDebugHandler2(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new SomeModuleDebugHandlersLogic(c).someDebugHandler2(i)
    },
    getDimension(c: HandlerContext): TrackingDimension {
        return new InterfaceForTrackingLogic(c).getDimension()
    },
}
