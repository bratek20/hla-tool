// DO NOT EDIT! Autogenerated by HLA tool

class OtherInterfaceMock implements OtherInterface {
    otherMethod(): void {
    }
}
namespace OtherModule.Mocks {
    export function createOtherInterfaceMock(): OtherInterfaceMock {
        return new OtherInterfaceMock()
    }
    export function setupOtherInterface(): OtherInterfaceMock {
        const mock = OtherModule.Mocks.createOtherInterfaceMock()
        OtherModule.Api.otherMethod = CreateMock(OtherModule.Api.otherMethod, () => { mock.otherMethod() })
        return mock
    }
}