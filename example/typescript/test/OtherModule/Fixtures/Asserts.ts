namespace OtherModule.Assert {
    export interface ExpectedOtherProperty {
        id?: string,
        name?: string,
    }
    export function otherProperty(given: OtherProperty, expected: ExpectedOtherProperty) {
        if (expected.id !== undefined) {
            AssertEquals(given.getId().value, expected.id)
        }

        if (expected.name !== undefined) {
            AssertEquals(given.name, expected.name)
        }
    }

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