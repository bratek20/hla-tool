package pl.bratek20.hla.definitions

import org.assertj.core.api.Assertions.assertThat
import pl.bratek20.hla.definitions.api.*

data class ExpectedPropertyMapping(
    var key: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertPropertyMapping(given: PropertyMapping, expectedInit: ExpectedPropertyMapping.() -> Unit) {
    val expected = ExpectedPropertyMapping().apply(expectedInit)

    expected.key?.let {
        assertThat(given.key).isEqualTo(it)
    }

    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedEnumDefinition(
    var name: String? = null,
    var values: List<String>? = null,
)
fun assertEnumDefinition(given: EnumDefinition, expectedInit: ExpectedEnumDefinition.() -> Unit) {
    val expected = ExpectedEnumDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.values?.let {
        assertThat(given.values).hasSize(it.size)
        given.values.forEachIndexed { idx, entry -> assertThat(entry).isEqualTo(it[idx]) }
    }
}

data class ExpectedModuleDefinition(
    var name: String? = null,
    var simpleValueObjects: List<(ExpectedSimpleStructureDefinition.() -> Unit)>? = null,
    var complexValueObjects: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var interfaces: List<(ExpectedInterfaceDefinition.() -> Unit)>? = null,
    var propertyValueObjects: List<(ExpectedComplexStructureDefinition.() -> Unit)>? = null,
    var propertyMappings: List<(ExpectedPropertyMapping.() -> Unit)>? = null,
    var enums: List<(ExpectedEnumDefinition.() -> Unit)>? = null,
)
fun assertModuleDefinition(given: ModuleDefinition, expectedInit: ExpectedModuleDefinition.() -> Unit) {
    val expected = ExpectedModuleDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name.value).isEqualTo(it)
    }

    expected.simpleValueObjects?.let {
        assertThat(given.simpleValueObjects).hasSize(it.size)
        given.simpleValueObjects.forEachIndexed { idx, entry -> assertSimpleStructureDefinition(entry, it[idx]) }
    }

    expected.complexValueObjects?.let {
        assertThat(given.complexValueObjects).hasSize(it.size)
        given.complexValueObjects.forEachIndexed { idx, entry -> assertComplexStructureDefinition(entry, it[idx]) }
    }

    expected.interfaces?.let {
        assertThat(given.interfaces).hasSize(it.size)
        given.interfaces.forEachIndexed { idx, entry -> assertInterfaceDefinition(entry, it[idx]) }
    }

    expected.propertyValueObjects?.let {
        assertThat(given.propertyValueObjects).hasSize(it.size)
        given.propertyValueObjects.forEachIndexed { idx, entry -> assertComplexStructureDefinition(entry, it[idx]) }
    }

    expected.propertyMappings?.let {
        assertThat(given.propertyMappings).hasSize(it.size)
        given.propertyMappings.forEachIndexed { idx, entry -> assertPropertyMapping(entry, it[idx]) }
    }

    expected.enums?.let {
        assertThat(given.enums).hasSize(it.size)
        given.enums.forEachIndexed { idx, entry -> assertEnumDefinition(entry, it[idx]) }
    }
}

data class ExpectedTypeDefinition(
    var name: String? = null,
    var wrappers: List<TypeWrapper>? = null,
)
fun assertTypeDefinition(given: TypeDefinition, expectedInit: ExpectedTypeDefinition.() -> Unit) {
    val expected = ExpectedTypeDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.wrappers?.let {
        assertThat(given.wrappers).hasSize(it.size)
        given.wrappers.forEachIndexed { idx, entry -> assertThat(entry).isEqualTo(it[idx]) }
    }
}

data class ExpectedFieldDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertFieldDefinition(given: FieldDefinition, expectedInit: ExpectedFieldDefinition.() -> Unit) {
    val expected = ExpectedFieldDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedSimpleStructureDefinition(
    var name: String? = null,
    var typeName: String? = null,
)
fun assertSimpleStructureDefinition(given: SimpleStructureDefinition, expectedInit: ExpectedSimpleStructureDefinition.() -> Unit) {
    val expected = ExpectedSimpleStructureDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.typeName?.let {
        assertThat(given.typeName).isEqualTo(it)
    }
}

data class ExpectedComplexStructureDefinition(
    var name: String? = null,
    var fields: List<(ExpectedFieldDefinition.() -> Unit)>? = null,
)
fun assertComplexStructureDefinition(given: ComplexStructureDefinition, expectedInit: ExpectedComplexStructureDefinition.() -> Unit) {
    val expected = ExpectedComplexStructureDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.fields?.let {
        assertThat(given.fields).hasSize(it.size)
        given.fields.forEachIndexed { idx, entry -> assertFieldDefinition(entry, it[idx]) }
    }
}

data class ExpectedInterfaceDefinition(
    var name: String? = null,
    var methods: List<(ExpectedMethodDefinition.() -> Unit)>? = null,
)
fun assertInterfaceDefinition(given: InterfaceDefinition, expectedInit: ExpectedInterfaceDefinition.() -> Unit) {
    val expected = ExpectedInterfaceDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.methods?.let {
        assertThat(given.methods).hasSize(it.size)
        given.methods.forEachIndexed { idx, entry -> assertMethodDefinition(entry, it[idx]) }
    }
}

data class ExpectedArgumentDefinition(
    var name: String? = null,
    var type: (ExpectedTypeDefinition.() -> Unit)? = null,
)
fun assertArgumentDefinition(given: ArgumentDefinition, expectedInit: ExpectedArgumentDefinition.() -> Unit) {
    val expected = ExpectedArgumentDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.type?.let {
        assertTypeDefinition(given.type, it)
    }
}

data class ExpectedExceptionDefinition(
    var name: String? = null,
)
fun assertExceptionDefinition(given: ExceptionDefinition, expectedInit: ExpectedExceptionDefinition.() -> Unit) {
    val expected = ExpectedExceptionDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }
}

data class ExpectedMethodDefinition(
    var name: String? = null,
    var returnType: (ExpectedTypeDefinition.() -> Unit)? = null,
    var args: List<(ExpectedArgumentDefinition.() -> Unit)>? = null,
    var throws: List<(ExpectedExceptionDefinition.() -> Unit)>? = null,
)
fun assertMethodDefinition(given: MethodDefinition, expectedInit: ExpectedMethodDefinition.() -> Unit) {
    val expected = ExpectedMethodDefinition().apply(expectedInit)

    expected.name?.let {
        assertThat(given.name).isEqualTo(it)
    }

    expected.returnType?.let {
        assertTypeDefinition(given.returnType, it)
    }

    expected.args?.let {
        assertThat(given.args).hasSize(it.size)
        given.args.forEachIndexed { idx, entry -> assertArgumentDefinition(entry, it[idx]) }
    }

    expected.throws?.let {
        assertThat(given.throws).hasSize(it.size)
        given.throws.forEachIndexed { idx, entry -> assertExceptionDefinition(entry, it[idx]) }
    }
}