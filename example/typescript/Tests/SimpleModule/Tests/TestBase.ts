namespace SimpleModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.SimpleModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("SimpleModule", testName, fun)
    }
}