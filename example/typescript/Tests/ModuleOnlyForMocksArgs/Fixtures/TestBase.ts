namespace ModuleOnlyForMocksArgs {
    export let context: HandlerContext

    export function setup(): void {
        context = EmptyContextFor(DependencyName.ModuleOnlyForMocksArgs)
    }
}