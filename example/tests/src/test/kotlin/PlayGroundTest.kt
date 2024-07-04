import com.some.pkg.somemodule.api.SomeIntWrapper
import com.some.pkg.somemodule.fixtures.assertSomeIntWrapper
import org.junit.jupiter.api.Test

//data class SomeIntWrapper(val value: Int) {
//    operator fun plus(other: SomeIntWrapper): SomeIntWrapper {
//        return SomeIntWrapper(this.value + other.value)
//    }
//
//    operator fun minus(other: SomeIntWrapper): SomeIntWrapper {
//        return SomeIntWrapper(this.value - other.value)
//    }
//
//    operator fun times(other: SomeIntWrapper): SomeIntWrapper {
//        return SomeIntWrapper(this.value * other.value)
//    }
//
//    override fun toString(): String {
//        return value.toString()
//    }
//}

class PlayGroundTest {
    @Test
    fun shouldSupportPlusOperatorForGeneratedSimpleVOBoxingIntType() {
        val v1 = SomeIntWrapper(1)
        val v2 = SomeIntWrapper(2)

        //assertSomeIntWrapper(v1 + v2, 3)
    }
}