namespace SomeModule.Assert {
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
    }
    export function someClass2(given: SomeClass2, expected: ExpectedSomeClass2) {
        if (expected.id !== undefined) {
            AssertEquals(given.id.value, expected.id)
        }
        if (expected.enabled !== undefined) {
            AssertEquals(given.enabled, expected.enabled)
        }
    }
}