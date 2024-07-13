package somemodule

import com.github.bratek20.architecture.context.someContextBuilder
import com.some.pkg.othermodule.fixtures.assertOtherClass
import com.some.pkg.othermodule.fixtures.otherClass
import com.some.pkg.somemodule.api.SomeInterface2
import com.some.pkg.somemodule.fixtures.SomeInterface2Mock
import com.some.pkg.somemodule.fixtures.SomeModuleMocks
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MocksTest {
    private lateinit var someInterface2: SomeInterface2
    private lateinit var someInterface2Mock: SomeInterface2Mock

    @BeforeEach
    fun setup() {
        val c = someContextBuilder().withModules(SomeModuleMocks()).build()

        someInterface2 = c.get(SomeInterface2::class.java)
        someInterface2Mock = c.get(SomeInterface2Mock::class.java)
    }

    @Test
    fun methodCalled() {
        someInterface2Mock.assertReferenceOtherClassCalled(times = 0)

        someInterface2.referenceOtherClass(otherClass())
        someInterface2Mock.assertReferenceOtherClassCalled()
    }

    @Test
    fun methodCalledForArgs() {
        someInterface2Mock.assertReferenceOtherClassCalledForArgs({}, 0)

        someInterface2.referenceOtherClass(otherClass { id = 1 })
        someInterface2Mock.assertReferenceOtherClassCalledForArgs({}, 1)
        someInterface2Mock.assertReferenceOtherClassCalledForArgs({ id = 1 }, 1)
        someInterface2Mock.assertReferenceOtherClassCalledForArgs({ id = 2 }, 0)
    }

    @Test
    fun setResult() {
        someInterface2.referenceOtherClass(otherClass()) shouldBe otherClass()

        someInterface2Mock.setReferenceOtherClassResponse({ id = 1 }, { id = 2 })
        someInterface2Mock.setReferenceOtherClassResponse({ id = 2; amount = 2 }, { id = 3 })
        someInterface2Mock.setReferenceOtherClassResponse({ amount = 3 }, { id = 4 })

        assertOtherClass(someInterface2.referenceOtherClass(otherClass { id = 1 })) {
            id = 2
        }

        assertOtherClass(someInterface2.referenceOtherClass(otherClass { id = 2; amount = 2 })) {
            id = 3
        }

        assertOtherClass(someInterface2.referenceOtherClass(otherClass { id = 2; amount = 3 })) {
            id = 4
        }

        //TODO remove
        assertOtherClass(someInterface2.referenceOtherClass(otherClass { id = 2; amount = 3 })) {
            id = 5
        }
    }
}