package somemodule

import com.github.bratek20.architecture.context.someContextBuilder
import com.some.pkg.othermodule.fixtures.assertOtherClass
import com.some.pkg.othermodule.fixtures.otherClass
import com.some.pkg.somemodule.api.SomeInterface2
import com.some.pkg.somemodule.fixtures.SomeInterface2Mock
import com.some.pkg.somemodule.fixtures.assertSomeClass2
import com.some.pkg.somemodule.fixtures.assertSomeClass3
import com.some.pkg.somemodule.fixtures.assertSomeClass6
import com.some.pkg.somemodule.fixtures.someClass2
import com.some.pkg.somemodule.fixtures.someClass3
import com.some.pkg.somemodule.fixtures.someClass6
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test

@Disabled
class AssertsTests {

    @Test
    fun listAssertion() {
        val given = someClass3 {
            class2List = listOf(
                {
                    id = "a"
                }
            )
        }

        assertSomeClass3(given) {
            class2List = listOf()
        }
    }

    @Test
    fun optionalAssertion() {
        assertSomeClass6(
            someClass6 {
                someClassOpt = null
                optString = null
            }, {
                someClassOpt = {
                    id = "a"
                }
                optString = "a"
            }
        )
    }
}