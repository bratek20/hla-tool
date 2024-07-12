import com.some.pkg.somemodule.api.SomeIntWrapper
import com.some.pkg.somemodule.fixtures.assertSomeClass6
import com.some.pkg.somemodule.fixtures.assertSomeIntWrapper
import com.some.pkg.somemodule.fixtures.someClass
import com.some.pkg.somemodule.fixtures.someClass6
import org.junit.jupiter.api.Test


class PlayGroundTest {
    @Test
    fun shouldSupportPlusOperatorForGeneratedSimpleVOBoxingIntType() {
        val v1 = SomeIntWrapper(1)
        val v2 = SomeIntWrapper(2)

        //assertSomeIntWrapper(v1 + v2, 3)
    }

    @Test
    fun shouldSupportEmptyInAsserts() {
        val someClass6 = someClass6 {
            someClassOpt = {}
            optString = null
        }

        assertSomeClass6(someClass6) {
            someClassOptEmpty = false
            optStringEmpty = true
        }
    }
}