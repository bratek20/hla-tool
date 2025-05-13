import com.some.pkg.somemodule.api.SomeId
import com.some.pkg.somemodule.api.SomeIntWrapper
import com.some.pkg.somemodule.fixtures.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test


class PlayGroundTest {
    @Test
    fun shouldSupportPlusMinusTimesOperatorForGeneratedSimpleVOBoxingIntType() {
        val v1 = SomeIntWrapper(1)
        val v2 = SomeIntWrapper(2)

        assertSomeIntWrapper(v1 + v2, 3)
        assertSomeIntWrapper(v1 - v2, -1)
        assertSomeIntWrapper(v1 * 3, 3)
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

    @Test
    fun mocksTests() {
        val mocks = SomeInterfaceMock()

        mocks.setSomeQueryResponse {
            id = "someId"
            amount = 14
        }
        assertSomeClass(mocks.someQuery(someQueryInput{})) {
            id = "someId"
            amount = 14
        }

        assertThat(mocks.optMethod(SomeId("1"))).isNull()

        mocks.setOptMethodResponse {
            id = "someId2"
            amount = 15
        }
        assertSomeClass(mocks.optMethod(SomeId("1"))!!) {
            id = "someId2"
            amount = 15
        }

        mocks.setMethodWithAnyResponse("some response")
        assertThat(mocks.methodWithAny("some input")).isEqualTo("some response")
    }
}