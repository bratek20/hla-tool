export let c: HandlerContext

export function setup(): void {
    c = EmptyContextFor(DependencyName.TypesModule)
}
