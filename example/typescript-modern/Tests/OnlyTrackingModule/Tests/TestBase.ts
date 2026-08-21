export let context: HandlerContext

export function setup(): void {
    context = EmptyContextFor(DependencyName.OnlyTrackingModule)
}

export function test(testName: string, fun: TestFunction) {
    addTest("OnlyTrackingModule", testName, fun)
}
