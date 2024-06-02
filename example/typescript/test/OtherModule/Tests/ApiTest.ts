namespace OtherModule {
    export function submoduleTest(submoduleName: string, testName: string, fun: TestFunction) {
        addSubmoduleTest("OtherModule", submoduleName, testName, fun);
    }

    function test(testName: string, fun: TestFunction) {
        submoduleTest("Api", testName, fun);
    }

    test("TODO", () => {
        AssertEquals(true, false, "TODO");
    });
}