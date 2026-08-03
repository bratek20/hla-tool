namespace OnlyInterfacesModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.OnlyInterfacesModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("OnlyInterfacesModule", testName, fun)
    }
}