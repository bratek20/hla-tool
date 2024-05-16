namespace TypesModule.Builder {
    export interface DateRangeDef {
        from?: string,
        to?: string,
    }
    export function dateRange(def?: DateRangeDef): DateRange {
        const from = def?.from ?? "someValue"
        const to = def?.to ?? "someValue"

        return CustomTypesMapper.dateRangeCreate(
            CustomTypesMapper.dateCreate(from),
            CustomTypesMapper.dateCreate(to),
        )
    }

    export interface DateRangePropertyDef {
        from?: string,
        to?: string,
    }
    export function dateRangeProperty(def?: DateRangePropertyDef): DateRangeProperty {
        const from = def?.from ?? "someValue"
        const to = def?.to ?? "someValue"

        return DateRangeProperty.create(
            from,
            to,
        )
    }
}