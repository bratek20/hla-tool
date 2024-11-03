package com.github.bratek20.hla.type.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.type.api.TypeApi
import com.github.bratek20.hla.type.context.TypeImpl
import com.github.bratek20.hla.type.fixtures.hlaType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TypeImplTest {
    @Test
    fun `should work`() {
        val api = someContextBuilder()
            .withModules(
                TypeImpl()
            )
            .get(TypeApi::class.java)

        api.getTypeDependencies(
            hlaType {  }
        ).let {
            assertThat(it).isEmpty()
        }
    }
}