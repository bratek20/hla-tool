import com.some.pkg.somemodule.api.SomeIntWrapper
import com.some.pkg.somemodule.fixtures.assertSomeIntWrapper
import org.junit.jupiter.api.Test


class PlayGroundTest {
    @Test
    fun shouldSupportPlusOperatorForGeneratedSimpleVOBoxingIntType() {
        val v1 = SomeIntWrapper(1)
        val v2 = SomeIntWrapper(2)

        assertSomeIntWrapper(v1 + v2, 3)
    }
}