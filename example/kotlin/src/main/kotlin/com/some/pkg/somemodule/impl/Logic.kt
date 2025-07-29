package com.some.pkg.somemodule.impl

import com.some.pkg.somemodule.api.*

import com.some.pkg.othermodule.api.*
import com.some.pkg.simplemodule.api.*
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

    override fun methodWithSimpleVO(id: SomeId): Unit {
        TODO("Not yet implemented")
    }

    override fun methodWithListOfSimpleVO(list: List<SomeId>): List<SomeId> {
        TODO("Not yet implemented")
    }

    override fun methodWithAny(i: Any): Any {
        TODO("Not yet implemented")
    }

    override fun methodWithBaseType(i: String): String {
        TODO("Not yet implemented")
    }

    override fun methodReturningOptSimpleVo(): SomeId? {
        TODO("Not yet implemented")
    }

    override fun methodReturningNumericType(): Int {
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

    override fun someHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        TODO("Not yet implemented")
    }
}

class SomeModuleDebugHandlersLogic: SomeModuleDebugHandlers {
    override fun someDebugHandler(i: SomeHandlerInput): SomeHandlerOutput {
        TODO("Not yet implemented")
    }

    override fun someDebugHandler2(i: SomeHandlerInput): SomeHandlerOutput {
        TODO("Not yet implemented")
    }
}

class InterfaceForTrackingLogic: InterfaceForTracking {
    override fun getDimension(): tracking.impl.TrackingDimension {
        TODO("Not yet implemented")
    }
}