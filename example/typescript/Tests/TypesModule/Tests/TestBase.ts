namespace TypesModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.TypesModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("TypesModule", testName, fun)
    }
}