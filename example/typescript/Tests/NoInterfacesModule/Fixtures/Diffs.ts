// DO NOT EDIT! Autogenerated by HLA tool

namespace NoInterfacesModule {
    export function diffNoInterfaceId(given: NoInterfaceId, expected: string, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }
}