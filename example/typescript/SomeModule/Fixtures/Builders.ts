namespace SomeModule.Builder {
    export interface SomeClassDef {
        id?: string,
        amount?: number,
    }
    export function someClass(def?: SomeClassDef): SomeClass {
        return new SomeClass(
            new SomeId(def?.id ?? "someValue"),
            def?.amount ?? 0,
        )
    }

    export interface SomeClass2Def {
        id?: string,
        enabled?: boolean,
        names?: string[],
        ids?: string[],
    }
    export function someClass2(def?: SomeClass2Def): SomeClass2 {
        return new SomeClass2(
            new SomeId(def?.id ?? "someValue"),
            def?.enabled ?? false,
            def?.names ?? [],
            (def?.ids ?? []).map(it => new SomeId(it)),
        )
    }

    export interface SomeClass3Def {
        class2Object?: SomeClass2Def,
        class2List?: SomeClass2Def[],
    }
    export function someClass3(def?: SomeClass3Def): SomeClass3 {
        return new SomeClass3(
            someClass2(def?.class2Object ?? {}),
            (def?.class2List ?? []).map(it => someClass2(it)),
        )
    }

    export interface SomeClass4Def {
        otherId?: string,
        otherClass?: OtherModule.Builder.OtherClassDef,
        otherIdList?: string[],
        otherClassList?: OtherModule.Builder.OtherClassDef[],
    }
    export function someClass4(def?: SomeClass4Def): SomeClass4 {
        return new SomeClass4(
            new OtherId(def?.otherId ?? "someValue"),
            OtherModule.Builder.otherClass(def?.otherClass ?? {}),
            (def?.otherIdList ?? []).map(it => new OtherId(it)),
            (def?.otherClassList ?? []).map(it => OtherModule.Builder.otherClass(it)),
        )
    }
}