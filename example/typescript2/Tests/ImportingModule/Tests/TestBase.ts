namespace ImportingModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.ImportingModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("ImportingModule", testName, fun)
    }
}