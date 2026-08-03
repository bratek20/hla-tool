namespace SomeUserModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.SomeUserModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("SomeUserModule", testName, fun)
    }
}