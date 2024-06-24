namespace Legacy {
    export function submoduleTest(submoduleName: string, testName: string, fun: TestFunction) {
        addSubmoduleTest("Legacy", submoduleName, testName, fun);
    }

    function test(testName: string, fun: TestFunction) {
        submoduleTest("Api", testName, fun);
    }

    test("TODO", () => {
        AssertEquals(true, false, "TODO");
    });
}