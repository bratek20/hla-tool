namespace OnlyTrackingModule {
    export let c: HandlerContext

    export function setup(): void {
        c = EmptyContextFor(DependencyName.OnlyTrackingModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("OnlyTrackingModule", testName, fun)
    }
}