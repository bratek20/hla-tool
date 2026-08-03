namespace TypesModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.TypesModule)
    }
}