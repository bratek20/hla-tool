namespace SimpleModule.Api {

    export function someMethod(i: SomeMethodInput, c: HandlerContext): SimpleData {
        return new Impl.SomeSimpleInterfaceLogic(c).someMethod(i)
    }

    export function notAddedToExamples(c: HandlerContext): void {
        new Impl.NotGeneratedInterfaceLogic(c).notAddedToExamples()
    }
}