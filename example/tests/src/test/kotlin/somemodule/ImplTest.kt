package somemodule

import com.github.bratek20.architecture.context.api.ContextBuilder
import com.github.bratek20.architecture.context.api.ContextModule
import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.architecture.exceptions.assertApiExceptionThrown
import com.some.pkg.somemodule.api.*
import com.some.pkg.somemodule.fixtures.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class TestSomeInterfaceLogic: SomeInterface {
    override fun someEmptyMethod() {
        TODO("Not yet implemented")
    }

    override fun someCommand(id: SomeId, amount: Int) {
        TODO("Not yet implemented")
    }

    override fun someQuery(query: SomeQueryInput): SomeClass {
        if (query.getId().value == "throw") {
            throw SomeException("Some message")
        }
        return SomeClass.create(
            id = query.getId(),
            amount = 0,
        )
    }

    override fun optMethod(optId: SomeId?): SomeClass? {
        TODO("Not yet implemented")
    }

    override fun methodWithListOfSimpleVO(list: List<SomeId>): List<SomeId> {
        return list
    }

}

class TestSomeModuleImpl: ContextModule {
    override fun apply(builder: ContextBuilder) {
        builder
            .setImpl(SomeInterface::class.java, TestSomeInterfaceLogic::class.java)
    }
}

open class SomeModuleImplTest {
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
        val result = someInterface.someQuery(
            someQueryInput {
                id = "id"
            }
        )

        assertSomeClass(result) {
            id = "id"
        }
    }

    @Test
    fun `should throw if id = throw`() {
        assertApiExceptionThrown(
            {
                someInterface.someQuery(
                    someQueryInput {
                        id = "throw"
                    }
                )
            },
            {
                type = SomeException::class
                message = "Some message"
            }
        )
    }

    @Test
    fun `should support opt lists`() {
        val c1 = classHavingOptList {}
        val c2 = classHavingOptList {
            optList = listOf {
                id = "id"
            }
        }

        assertClassHavingOptList(c1) {
            optListEmpty = true
        }

        assertClassHavingOptList(c2) {
            optList = listOf {
                id = "id"
            }
        }
    }

    @Test
    fun `should support list of simple vo`() {
        val result = someInterface.methodWithListOfSimpleVO(
            listOf(
                someId("id1"),
                someId("id2")
            )
        )

        assert(result.size == 2)
        assert(result[0].value == "id1")
        assert(result[1].value == "id2")
    }
}