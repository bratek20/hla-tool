namespace ModuleOnlyForMocksArgs {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.ModuleOnlyForMocksArgs)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("ModuleOnlyForMocksArgs", testName, fun)
    }
}