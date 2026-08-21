namespace TypesModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.TypesModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("TypesModule", testName, fun)
    }
}