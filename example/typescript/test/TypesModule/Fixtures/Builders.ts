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
}