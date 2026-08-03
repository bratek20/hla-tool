namespace ImportingModule {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.ImportingModule)
    }
}