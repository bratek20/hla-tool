namespace TypeModule.Assert {
    export interface ExpectedDateRange {
        from?: string,
        to?: string,
    }
    export function dateRange(given: DateRange, expected: ExpectedDateRange) {
        if (expected.from !== undefined) {
            AssertEquals(dateGetValue(dateRangeGetFrom(given)), expected.from)
        }

        if (expected.to !== undefined) {
            AssertEquals(dateGetValue(dateRangeGetTo(given)), expected.to)
        }
    }
}