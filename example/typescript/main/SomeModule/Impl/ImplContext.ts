namespace SomeModule.Api {

    export function someEmptyMethod(c: HandlerContext): void {
        new Impl.SomeInterfaceLogic(c).someEmptyMethod()
    }

    export function someCommand(id: SomeId, amount: number, c: HandlerContext): void {
        new Impl.SomeInterfaceLogic(c).someCommand(id, amount)
    }

    export function someQuery(query: SomeQueryInput, c: HandlerContext): SomeClass {
        return new Impl.SomeInterfaceLogic(c).someQuery(query)
    }

    export function optMethod(optId: Optional<SomeId>, c: HandlerContext): Optional<SomeClass> {
        return new Impl.SomeInterfaceLogic(c).optMethod(optId)
    }

    export function methodWithListOfSimpleVO(list: SomeId[], c: HandlerContext): SomeId[] {
        return new Impl.SomeInterfaceLogic(c).methodWithListOfSimpleVO(list)
    }

    export function methodWithAny(i: any, c: HandlerContext): any {
        return new Impl.SomeInterfaceLogic(c).methodWithAny(i)
    }

    export function methodReturningOptSimpleVo(c: HandlerContext): Optional<SomeId> {
        return new Impl.SomeInterfaceLogic(c).methodReturningOptSimpleVo()
    }

    export function referenceOtherClass(other: OtherClass, c: HandlerContext): OtherClass {
        return new Impl.SomeInterface2Logic(c).referenceOtherClass(other)
    }

    export function referenceLegacyType(legacyType: LegacyType, c: HandlerContext): LegacyType {
        return new Impl.SomeInterface2Logic(c).referenceLegacyType(legacyType)
    }

    export function referenceInterface(empty: SomeEmptyInterface, c: HandlerContext): SomeEmptyInterface {
        return new Impl.SomeInterface3Logic(c).referenceInterface(empty)
    }

    export function referenceOtherInterface(other: OtherInterface, c: HandlerContext): OtherInterface {
        return new Impl.SomeInterface3Logic(c).referenceOtherInterface(other)
    }

    export function someHandler(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new Impl.SomeModuleHandlersLogic(c).someHandler(i)
    }

    export function someHandler2(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new Impl.SomeModuleHandlersLogic(c).someHandler2(i)
    }

    export function someDebugHandler(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new Impl.SomeModuleDebugHandlersLogic(c).someDebugHandler(i)
    }

    export function someDebugHandler2(i: SomeHandlerInput, c: HandlerContext): SomeHandlerOutput {
        return new Impl.SomeModuleDebugHandlersLogic(c).someDebugHandler2(i)
    }

    export function getDimension(c: HandlerContext): TrackingDimension {
        return new Impl.InterfaceForTrackingLogic(c).getDimension()
    }
}