namespace OtherModule {
    export let context: HandlerContext

    export interface SetupArgs {
        otherProperty?: OtherModule.Builder.OtherPropertyDef
        otherProperties?: OtherModule.Builder.OtherPropertyDef[]
    }

    export function setup(args: SetupArgs = {}): void {
        context = Ts.E2E.SetupAndCreateContext({
            dependencyName: DependencyName.OtherModule,
            titleData: builderTD => {
                builderTD.with(OTHER_PROPERTY_PROPERTY_KEY, OtherModule.Builder.otherProperty(args.otherProperty ?? {}))
                builderTD.with(OTHER_PROPERTIES_PROPERTY_KEY, (args.otherProperties ?? []).map(it => OtherModule.Builder.otherProperty(it)))
            }
        }).context
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("OtherModule", testName, fun)
    }
}