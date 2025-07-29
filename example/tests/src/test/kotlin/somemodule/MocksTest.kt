package somemodule

import com.github.bratek20.architecture.context.someContextBuilder
import com.some.pkg.othermodule.fixtures.assertOtherClass
import com.some.pkg.othermodule.fixtures.otherClass
import com.some.pkg.somemodule.api.SomeInterface2
import com.some.pkg.somemodule.fixtures.SomeInterface2Mock
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class MocksTest {

    private lateinit var someInterface2: SomeInterface2
    private lateinit var someInterface2Mock: SomeInterface2Mock

    @BeforeEach
    fun setup() {
        val c = someContextBuilder()
            .setImpl(SomeInterface2::class.java, SomeInterface2Mock::class.java)
            .build()

        someInterface2 = c.get(SomeInterface2::class.java)
        someInterface2Mock = c.get(SomeInterface2Mock::class.java)
    }

    @Test
    fun methodCalled() {
        someInterface2Mock.assertReferenceOtherClassCallsNumber(0)

        someInterface2.referenceOtherClass(otherClass())
        someInterface2Mock.assertReferenceOtherClassCallsNumber(1)
    }

    @Test
    fun methodCalledForArgs() {
        someInterface2Mock.assertReferenceOtherClassCalls(emptyList())

        someInterface2.referenceOtherClass(otherClass { id = 1 })
        someInterface2Mock.assertReferenceOtherClassCalls(listOf {
            id = 1
        })
    }

    @Test
    fun setResult() {
        assertThat(someInterface2.referenceOtherClass(otherClass())).isEqualTo(otherClass())

        someInterface2Mock.setReferenceOtherClassResponse {
            id = 1
        }

        assertOtherClass(someInterface2.referenceOtherClass(otherClass())) {
            id = 1
        }
    }
}