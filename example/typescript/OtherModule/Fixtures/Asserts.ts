namespace OtherModule.Assert {
    export interface ExpectedOtherClass {
        id?: string,
        amount?: number,
    }
    export function otherClass(given: OtherClass, expected: ExpectedOtherClass) {
        if (expected.id !== undefined) {
            AssertEquals(given.id.value, expected.id)
        }
        if (expected.amount !== undefined) {
            AssertEquals(given.amount, expected.amount)
        }
    }
}