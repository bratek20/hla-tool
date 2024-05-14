namespace TypesModule.Builder {
    export interface DateRangeDef {
        from?: string,
        to?: string,
    }
    export function dateRange(def?: DateRangeDef): DateRange {
        return CustomTypesMapper.dateRangeCreate(
            CustomTypesMapper.dateCreate(def?.from ?? "someValue"),
            CustomTypesMapper.dateCreate(def?.to ?? "someValue"),
        )
    }

    export interface DateRangePropertyDef {
        from?: string,
        to?: string,
    }
    export function dateRangeProperty(def?: DateRangePropertyDef): DateRangeProperty {
        return DateRangeProperty.create(
            def?.from ?? "someValue",
            def?.to ?? "someValue",
        )
    }
}