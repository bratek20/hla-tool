namespace SomeUserModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.SomeUserModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("SomeUserModule", testName, fun)
    }
}