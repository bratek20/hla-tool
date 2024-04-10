namespace SomeModule.Builder {
    export interface SomeClassDef {
        id?: string,
        amount?: number,
    }
    export function someClass(def?: SomeClassDef): SomeClass {
        return new SomeClass(
            new SomeId(def?.id ?? "someValue"),
            def?.amount ?? 0
        )
    }

    export interface SomeClass2Def {
        id?: string,
        enabled?: boolean,
    }
    export function someClass2(def?: SomeClass2Def): SomeClass2 {
        return new SomeClass2(
            new SomeId(def?.id ?? "someValue"),
            def?.enabled ?? false
        )
    }
}