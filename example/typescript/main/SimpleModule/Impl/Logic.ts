namespace SimpleModule.Impl {
    export class SomeSimpleInterfaceLogic implements SomeSimpleInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        someMethod(i: SomeMethodInput): SimpleData {
            // TODO
            return undefined
        }
    }

    export class NotGeneratedInterfaceLogic implements NotGeneratedInterface {
        constructor(
            private readonly c: HandlerContext,
        ) {}

        notAddedToExamples(): void {
            // TODO
            return undefined
        }
    }
}