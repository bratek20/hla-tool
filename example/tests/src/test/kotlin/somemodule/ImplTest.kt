package somemodule

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.some.pkg.somemodule.api.*
import com.some.pkg.somemodule.fixtures.assertSomeClass
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

class TestSomeInterfaceLogic: SomeInterface {
    override fun someEmptyMethod() {
        TODO("Not yet implemented")
    }

    override fun someCommand(id: SomeId, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun someQuery(id: SomeId): SomeClass {
        if (id.value == "throw") {
            throw SomeException("Some message")
        }
        return SomeClass.create(
            id = id,
            amount = 0,
        )
    }

    override fun optMethod(optId: SomeId?): SomeClass? {
        TODO("Not yet implemented")
    }

}

class TestSomeModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeInterface::class.java, TestSomeInterfaceLogic::class.java)
    }
}

open class TestSomeModuleImplTest {
    open fun createSomeInterface(): SomeInterface {
        return someContextBuilder()
            .withModules(TestSomeModuleImpl())
            .get(SomeInterface::class.java)
    }

    private lateinit var someInterface: SomeInterface

    @BeforeEach
    fun setup() {
        someInterface = createSomeInterface()
    }

    @Test
    fun `should copy id for someQuery`() {
        val result = someInterface.someQuery(SomeId("id"))

        assertSomeClass(result) {
            id = "id"
        }
    }

    @Disabled("Passing exceptions in web layer missing")
    @Test
    fun `should throw if id = throw`() {
        assertApiExceptionThrown(
            { someInterface.someQuery(SomeId("throw")) },
            {
                type = SomeException::class
                message = "Some message"
            }
        )
    }
}