// DO NOT EDIT! Autogenerated by HLA tool

namespace OtherModule.Assert {
    export function otherId(given: OtherId, expected: number) {
        const diff = diffOtherId(given, expected)
        AssertEquals(diff, "", diff)
    }

    export function otherProperty(given: OtherProperty, expected: ExpectedOtherProperty) {
        const diff = diffOtherProperty(given, expected)
        AssertEquals(diff, "", diff)
    }

    export function otherClass(given: OtherClass, expected: ExpectedOtherClass) {
        const diff = diffOtherClass(given, expected)
        AssertEquals(diff, "", diff)
    }

    export function otherData(given: OtherData, expected: ExpectedOtherData) {
        const diff = diffOtherData(given, expected)
        AssertEquals(diff, "", diff)
    }
}