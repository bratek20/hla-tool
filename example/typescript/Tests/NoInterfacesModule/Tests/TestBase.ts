namespace NoInterfacesModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.NoInterfacesModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("NoInterfacesModule", testName, fun)
    }
}