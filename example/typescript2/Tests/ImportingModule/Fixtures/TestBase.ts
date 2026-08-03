namespace ImportingModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.ImportingModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("ImportingModule", testName, fun)
    }
}