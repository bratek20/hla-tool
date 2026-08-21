namespace ModuleOnlyForMocksArgs {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.ModuleOnlyForMocksArgs)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("ModuleOnlyForMocksArgs", testName, fun)
    }
}