package com.some.pkg.somemodule.impl

import com.some.pkg.somemodule.api.*

class SomeEmptyInterfaceLogic: SomeEmptyInterface {
}
class SomeInterfaceLogic: SomeInterface {
    override fun someCommand(id: SomeId, amount: Int): Unit {
        TODO("Not yet implemented")
    }

    override fun someQuery(id: SomeId): SomeClass {
        TODO("Not yet implemented")
    }

    override fun optMethod(optId: SomeId?): SomeClass? {
        TODO("Not yet implemented")
    }
}
class SomeInterface2Logic: SomeInterface2 {
    override fun referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface {
        TODO("Not yet implemented")
    }

    override fun referenceOtherInterface(other: OtherInterface): OtherInterface {
        TODO("Not yet implemented")
    }
}