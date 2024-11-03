package com.github.bratek20.hla.types.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.types.api.TypesApi
import com.github.bratek20.hla.types.context.TypeImpl
import com.github.bratek20.hla.types.fixtures.hlaType
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class TypeImplTest {
    @Test
    fun `should work`() {
        val api = someContextBuilder()
            .withModules(
                TypeImpl()
            )
            .get(TypesApi::class.java)

        api.getTypeDependencies(
            hlaType {  }
        ).let {
            assertThat(it).isEmpty()
        }
    }
}