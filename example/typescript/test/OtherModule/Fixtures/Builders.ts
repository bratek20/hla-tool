namespace OtherModule.Builder {
    export interface OtherClassDef {
        id?: string,
        amount?: number,
    }
    export function otherClass(def?: OtherClassDef): OtherClass {
        return new OtherClass(
            new OtherId(def?.id ?? "someValue"),
            def?.amount ?? 0,
        )
    }

    export interface OtherPropertyDef {
        id?: string,
        name?: string,
    }
    export function otherProperty(def?: OtherPropertyDef): OtherProperty {
        return OtherProperty.create(
            def?.id ?? "someValue",
            def?.name ?? "someValue",
        )
    }
}