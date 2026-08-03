namespace WebNoWrappers {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.WebNoWrappers)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("WebNoWrappers", testName, fun)
    }
}