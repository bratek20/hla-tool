// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.othermodule.fixtures
class OtherInterfaceMock: OtherInterface {
    private var otherMethodCalls: Int = 0
    fun otherMethod(): Unit {
        otherMethodCalls = otherMethodCalls + 1
    }
    fun assertOtherMethodCalls(expectedNumber: Int) {
        AssertEquals(otherMethodCalls, expectedNumber, "Expected 'otherMethod' to be called " + expectedNumber + " times but was called " + otherMethodCalls + " times")
    }
    fun reset() {
        otherMethodCalls = 0
    }
}

namespace OtherModule.Mocks {
    export fun createOtherInterfaceMock(): OtherInterfaceMock {
        return OtherInterfaceMock()
    }

    export fun setupOtherInterface(): OtherInterfaceMock {
        val mock = OtherModule.Mocks.createOtherInterfaceMock()
        OtherModule.Api.otherMethod = CreateMock(OtherModule.Api.otherMethod, () => { mock.otherMethod() })
        return mock
    }
}