// DO NOT EDIT! Autogenerated by HLA tool

package com.some.pkg.somemodule.api

import com.some.pkg.othermodule.api.*
import com.some.pkg.typesmodule.api.*

interface SomeEmptyInterface {
}

interface SomeInterface {
    fun someEmptyMethod(): Unit

    @Throws(
        SomeException::class,
        Some2Exception::class,
    )
    fun someCommand(id: SomeId, amount: Int): Unit

    @Throws(
        SomeException::class,
    )
    fun someQuery(query: SomeQueryInput): SomeClass

    fun optMethod(optId: SomeId?): SomeClass?
}

interface SomeInterface2 {
    fun referenceOtherClass(other: OtherClass): OtherClass

    fun referenceLegacyType(legacyType: com.some.pkg.legacy.LegacyType): com.some.pkg.legacy.LegacyType
}

interface SomeInterface3 {
    fun referenceInterface(empty: SomeEmptyInterface): SomeEmptyInterface

    fun referenceOtherInterface(other: OtherInterface): OtherInterface
}

interface SomeModuleHandlers {
    @Throws(
        SomeException::class,
        Some2Exception::class,
    )
    fun someHandler(i: SomeHandlerInput): SomeHandlerOutput

    fun someHandler2(i: SomeHandlerInput): SomeHandlerOutput
}

interface SomeModuleDebugHandlers {
    fun someDebugHandler(i: SomeHandlerInput): SomeHandlerOutput

    fun someDebugHandler2(i: SomeHandlerInput): SomeHandlerOutput
}