namespace TypeModule.Assert {
    export interface ExpectedDateRange {
        from?: string,
        to?: string,
    }
    export function dateRange(given: DateRange, expected: ExpectedDateRange) {
        if (expected.from !== undefined) {
            AssertEquals(getDateValue(getDateRangeFrom(given)), expected.from)
        }

        if (expected.to !== undefined) {
            AssertEquals(getDateValue(getDateRangeTo(given)), expected.to)
        }
    }
}