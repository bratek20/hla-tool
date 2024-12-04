import com.github.bratek20.architecture.structs.api.struct
import com.some.pkg.somemodule.fixtures.assertSomeProperty
import com.some.pkg.somemodule.fixtures.diffSomeProperty
import com.some.pkg.somemodule.fixtures.someProperty
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class StructAssertsTest {
    private val obj = someProperty {
        customData = struct {
            "value" to "some value"
            "otherValue" to "other value"
        }
    }

    @Test
    fun `exact equals`() {
        assertSomeProperty(obj) {
            customData = struct {
                "value" to "some value"
                "otherValue" to "other value"
            }
        }
    }

    @Test
    fun `BUG - partial equals not supported`() {
        val diff = diffSomeProperty(obj, {
            customData = struct {
                "value" to "some value"
            }
        })

        assertThat(diff)
            .isEqualTo("customData {value=some value, otherValue=other value} != {value=some value}")
    }

    @Test
    fun `not equals`() {
        val diff = diffSomeProperty(obj, {
            customData = struct {
                "value" to "some value2"
                "otherValue" to "other value"
            }
        })

        assertThat(diff)
            .isEqualTo("customData {value=some value, otherValue=other value} != {value=some value2, otherValue=other value}")
    }
}