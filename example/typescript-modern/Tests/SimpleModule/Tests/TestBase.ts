export let context: HandlerContext

export function setup(): void {
    context = EmptyContextFor(DependencyName.SimpleModule)
}

export function test(testName: string, fun: TestFunction) {
    addTest("SimpleModule", testName, fun)
}
