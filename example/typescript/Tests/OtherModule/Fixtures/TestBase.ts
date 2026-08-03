namespace OtherModule {
    export let context: HandlerContext

    export interface SetupArgs {
        otherProperty?: OtherModule.Builder.OtherPropertyDef
        otherProperties?: OtherModule.Builder.OtherPropertyDef[]
    }

    export function setup(args?: SetupArgs): void {
        context = EmptyContextFor(DependencyName.OtherModule)
    }
}