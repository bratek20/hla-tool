namespace OnlyInterfacesModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.OnlyInterfacesModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("OnlyInterfacesModule", testName, fun)
    }
}