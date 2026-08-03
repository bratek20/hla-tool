namespace OtherModule {
    export let context: HandlerContext

    export interface SetupArgs {
        otherProperty?: OtherModule.Builder.OtherPropertyDef
        otherProperties?: OtherModule.Builder.OtherPropertyDef[]
    }

    export function setup(args?: SetupArgs): void {
        context = EmptyContextFor(DependencyName.OtherModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("OtherModule", testName, fun)
    }
}