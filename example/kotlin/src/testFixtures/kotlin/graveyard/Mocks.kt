// DO NOT EDIT! Autogenerated by HLA tool

package graveyard

import org.assertj.core.api.Assertions.assertThat

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule

import com.some.pkg.othermodule.api.*
import com.some.pkg.othermodule.fixtures.*

import com.some.pkg.somemodule.api.*

class SomeInterface2Mock: SomeInterface2 {
    // referenceOtherClass
    private val referenceOtherClassCalls: MutableList<OtherClass> = mutableListOf()
    private val referenceOtherClassResponses: MutableList<Pair<(ExpectedOtherClass.() -> Unit), (OtherClassDef.() -> Unit)>> = mutableListOf()

    fun setReferenceOtherClassResponse(args: (ExpectedOtherClass.() -> Unit), response: (OtherClassDef.() -> Unit)) {
        referenceOtherClassResponses.add(Pair(args, response))
    }

    override fun referenceOtherClass(other: OtherClass): OtherClass {
        referenceOtherClassCalls.add(other)
        val findResult = referenceOtherClassResponses.find { it -> diffOtherClass(other, it.first) == "" }
        return otherClass(findResult?.second ?: {})
    }

    fun assertReferenceOtherClassCalled(times: Int = 1) {
        assertThat(referenceOtherClassCalls.size).withFailMessage("Expected referenceOtherClass to be called $times times, but was called $referenceOtherClassCalls times").isEqualTo(times)
    }

    fun assertReferenceOtherClassCalledForArgs(args: (ExpectedOtherClass.() -> Unit), times: Int = 1) {
        val calls = referenceOtherClassCalls.filter { diffOtherClass(it, args) == "" }
        assertThat(calls.size).withFailMessage("Expected referenceOtherClass to be called $times times, but was called $referenceOtherClassCalls times").isEqualTo(times)
    }
    // referenceLegacyType
    private val referenceLegacyTypeCalls: MutableList<com.some.pkg.legacy.LegacyType> = mutableListOf()
    private val referenceLegacyTypeResponses: MutableList<Pair<com.some.pkg.legacy.LegacyType, com.some.pkg.legacy.LegacyType>> = mutableListOf()

    fun setReferenceLegacyTypeResponse(args: com.some.pkg.legacy.LegacyType, response: com.some.pkg.legacy.LegacyType) {
        referenceLegacyTypeResponses.add(Pair(args, response))
    }

    override fun referenceLegacyType(legacyType: com.some.pkg.legacy.LegacyType): com.some.pkg.legacy.LegacyType {
        TODO()
    }

    fun assertReferenceLegacyTypeCalled(times: Int = 1) {
        assertThat(referenceLegacyTypeCalls.size).withFailMessage("Expected referenceLegacyType to be called $times times, but was called $referenceLegacyTypeCalls times").isEqualTo(times)
    }
}

class SomeModuleMocks: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeInterface2::class.java, SomeInterface2Mock::class.java)
    }
}