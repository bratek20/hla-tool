import { describe, test } from "vitest"
import { test as NoInterfacesModuleTest } from "./TestBase"

describe("NoInterfacesModule - Api", () => {
    NoInterfacesModuleTest("TODO", () => {
        AssertEquals(true, false, "TODO")
    })
})
