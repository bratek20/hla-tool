export let context: HandlerContext

export function setup(): void {
    context = EmptyContextFor(DependencyName.NoInterfacesModule)
}

export function test(testName: string, fun: TestFunction) {
    addTest("NoInterfacesModule", testName, fun)
}
