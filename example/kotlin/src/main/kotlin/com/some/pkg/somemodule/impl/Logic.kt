package com.some.pkg.somemodule.impl

import com.some.pkg.somemodule.api.*

import com.some.pkg.othermodule.api.*
import com.some.pkg.typesmodule.api.*

class SomeEmptyInterfaceLogic: SomeEmptyInterface {
}

class SomeInterfaceLogic: SomeInterface {
    override fun someEmptyMethod(): Unit {
        TODO("Not yet implemented")
    }

    override fun someCommand(id: SomeId, amount: Int): Unit {
        TODO("Not yet implemented")
    }

    override fun someQuery(query: SomeQueryInput): SomeClass {
        TODO("Not yet implemented")
    }

    override fun optMethod(optId: SomeId?): SomeClass? {
        TODO("Not yet implemented")
    }
}

class SomeInterface2Logic: SomeInterface2 {
    override fun referenceOtherClass(other: OtherClass): OtherClass {
        TODO("Not yet implemented")
    }

    override fun referenceLegacyType(legacyType: com.some.pkg.legacy.LegacyType): com.some.pkg.legacy.LegacyType {
        TODO("Not yet implemented")
    }
}

class SomeInterface3Logic: SomeInterface3 {
    override fun referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface {
        TODO("Not yet implemented")
    }

    override fun referenceOtherInterface(other: OtherInterface): OtherInterface {
        TODO("Not yet implemented")
    }
}

class SomeModuleHandlersLogic: SomeModuleHandlers {
    override fun someHandler(i: SomeHandlerInput): SomeHandlerOutput {
        TODO("Not yet implemented")
    }
}