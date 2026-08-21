namespace WebNoWrappers {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.WebNoWrappers)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("WebNoWrappers", testName, fun)
    }
}