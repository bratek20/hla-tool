// DO NOT EDIT! Autogenerated by HLA tool

namespace SimpleModule {
    export function diffSimpleId(given: SimpleId, expected: string, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }

    export function diffSomeLongWrapper(given: SomeLongWrapper, expected: number, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }
}