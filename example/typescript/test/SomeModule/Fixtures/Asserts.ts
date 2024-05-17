namespace SomeModule.Assert {
    export interface ExpectedDateRangeWrapper {
        range?: TypesModule.Assert.ExpectedDateRange,
    }
    export function dateRangeWrapper(given: DateRangeWrapper, expected: ExpectedDateRangeWrapper) {
        if (expected.range !== undefined) {
            TypesModule.Assert.dateRange(CustomTypesMapper.dateRangeWrapperGetRange(given), expected.range)
        }
    }

    export interface ExpectedSomeClass {
        id?: string,
        amount?: number,
    }
    export function someClass(given: SomeClass, expected: ExpectedSomeClass) {
        if (expected.id !== undefined) {
            AssertEquals(given.id.value, expected.id)
        }

        if (expected.amount !== undefined) {
            AssertEquals(given.amount, expected.amount)
        }
    }

    export interface ExpectedSomeClass2 {
        id?: string,
        enabled?: boolean,
        names?: string[],
        ids?: string[],
    }
    export function someClass2(given: SomeClass2, expected: ExpectedSomeClass2) {
        if (expected.id !== undefined) {
            AssertEquals(given.id.value, expected.id)
        }

        if (expected.enabled !== undefined) {
            AssertEquals(given.enabled, expected.enabled)
        }

        if (expected.names !== undefined) {
            AssertEquals(given.names.length, expected.names.length)
            given.names.forEach((entry, idx) => AssertEquals(entry, expected.names[idx]))
        }

        if (expected.ids !== undefined) {
            AssertEquals(given.ids.length, expected.ids.length)
            given.ids.forEach((entry, idx) => AssertEquals(entry.value, expected.ids[idx]))
        }
    }

    export interface ExpectedSomeClass3 {
        class2Object?: ExpectedSomeClass2,
        class2List?: ExpectedSomeClass2[],
        someEnum?: SomeEnum,
    }
    export function someClass3(given: SomeClass3, expected: ExpectedSomeClass3) {
        if (expected.class2Object !== undefined) {
            someClass2(given.class2Object, expected.class2Object)
        }

        if (expected.class2List !== undefined) {
            AssertEquals(given.class2List.length, expected.class2List.length)
            given.class2List.forEach((entry, idx) => someClass2(entry, expected.class2List[idx]))
        }

        if (expected.someEnum !== undefined) {
            AssertEquals(given.someEnum, expected.someEnum)
        }
    }

    export interface ExpectedSomeClass4 {
        otherId?: string,
        otherClass?: OtherModule.Assert.ExpectedOtherClass,
        otherIdList?: string[],
        otherClassList?: OtherModule.Assert.ExpectedOtherClass[],
    }
    export function someClass4(given: SomeClass4, expected: ExpectedSomeClass4) {
        if (expected.otherId !== undefined) {
            AssertEquals(given.otherId.value, expected.otherId)
        }

        if (expected.otherClass !== undefined) {
            OtherModule.Assert.otherClass(given.otherClass, expected.otherClass)
        }

        if (expected.otherIdList !== undefined) {
            AssertEquals(given.otherIdList.length, expected.otherIdList.length)
            given.otherIdList.forEach((entry, idx) => AssertEquals(entry.value, expected.otherIdList[idx]))
        }

        if (expected.otherClassList !== undefined) {
            AssertEquals(given.otherClassList.length, expected.otherClassList.length)
            given.otherClassList.forEach((entry, idx) => OtherModule.Assert.otherClass(entry, expected.otherClassList[idx]))
        }
    }

    export interface ExpectedSomeClass5 {
        date?: string,
        dateRange?: TypesModule.Assert.ExpectedDateRange,
        dateRangeWrapper?: ExpectedDateRangeWrapper,
        someProperty?: ExpectedSomeProperty,
        otherProperty?: OtherModule.Assert.ExpectedOtherProperty,
    }
    export function someClass5(given: SomeClass5, expected: ExpectedSomeClass5) {
        if (expected.date !== undefined) {
            AssertEquals(TypesModule.CustomTypesMapper.dateGetValue(given.date), expected.date)
        }

        if (expected.dateRange !== undefined) {
            TypesModule.Assert.dateRange(given.dateRange, expected.dateRange)
        }

        if (expected.dateRangeWrapper !== undefined) {
            dateRangeWrapper(given.dateRangeWrapper, expected.dateRangeWrapper)
        }

        if (expected.someProperty !== undefined) {
            someProperty(given.someProperty, expected.someProperty)
        }

        if (expected.otherProperty !== undefined) {
            OtherModule.Assert.otherProperty(given.otherProperty, expected.otherProperty)
        }
    }
}