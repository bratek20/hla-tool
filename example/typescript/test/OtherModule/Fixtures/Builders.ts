namespace OtherModule.Builder {
    export interface OtherPropertyDef {
        id?: string,
        name?: string,
    }
    export function otherProperty(def?: OtherPropertyDef): OtherProperty {
        const id = def?.id ?? "someValue"
        const name = def?.name ?? "someValue"

        return OtherProperty.create(
            id,
            name,
        )
    }

    export interface OtherClassDef {
        id?: string,
        amount?: number,
    }
    export function otherClass(def?: OtherClassDef): OtherClass {
        const id = def?.id ?? "someValue"
        const amount = def?.amount ?? 0

        return new OtherClass(
            new OtherId(id),
            amount,
        )
    }
}