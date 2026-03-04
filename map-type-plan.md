# Map Type Support Implementation Plan

## Overview
Add support for map types with syntax `<Key, Value>` (e.g., `myMap: <string, int>`) to the HLA framework, focusing primarily on Kotlin generation. Maps should work with value objects, asserts, and builders patterns.

## Syntax
- Map: `<Key, Value>` → `myMap: <string, int>`
- Optional Map: `<Key, Value>?` → `myMap: <string, int>?`

## Implementation Steps

### 1. Add MAP TypeWrapper Enum
**File**: `lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/Enums.kt`

Add `MAP` to the `TypeWrapper` enum:
```kotlin
enum class TypeWrapper {
    LIST,
    OPTIONAL,
    MAP,  // New
}
```

### 2. Update Type Parsing
**File**: `lib/src/main/kotlin/com/github/bratek20/hla/parsing/impl/ModuleGroupParserLogic.kt`

Update `parseType()` method around lines 366-389 to recognize `<Key, Value>` syntax:

```kotlin
private fun parseType(typeValue: String): TypeDefinition {
    // Check for map type: <Key, Value> or <Key, Value>?
    val mapPattern = Regex("""<([^,]+),\s*([^>]+)>(\??)""")
    val mapMatch = mapPattern.find(typeValue)

    if (mapMatch != null) {
        val keyType = mapMatch.groupValues[1].trim()
        val valueType = mapMatch.groupValues[2].trim()
        val isOptional = mapMatch.groupValues[3] == "?"

        // Create a map type with special naming convention
        val mapTypeName = "MAP<$keyType,$valueType>"
        val wrappers = mutableListOf(TypeWrapper.MAP)
        if (isOptional) {
            wrappers.add(0, TypeWrapper.OPTIONAL)
        }

        return TypeDefinition.create(
            name = mapTypeName,
            wrappers = wrappers
        )
    }

    // Existing logic for []?, ?, []
    if (typeValue.contains("[]?")) {
        return TypeDefinition.create(
            name = typeValue.replace("[]?", ""),
            wrappers = listOf(TypeWrapper.OPTIONAL, TypeWrapper.LIST)
        )
    }
    // ... rest of existing logic
}
```

**Note**: The map type name format `"MAP<KeyType,ValueType>"` stores both key and value types for later extraction.

### 3. Add MapApiType Class
**File**: `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypes.kt`

Add new `MapApiType` class similar to `ListApiType` (around line 700):

```kotlin
class MapApiType(
    private val keyType: ApiTypeLogic,
    private val valueType: ApiTypeLogic,
) : ApiTypeLogic {

    override fun builder(): TypeBuilder {
        return mapType(keyType.builder(), valueType.builder())
    }

    override fun serializableBuilder(): TypeBuilder {
        // In serialization, maps are typically Map<Key, Value>
        return mapType(keyType.serializableBuilder(), valueType.serializableBuilder())
    }

    override fun modernDeserialize(
        context: ModernDeserializeContext,
        variable: ExpressionBuilder
    ): ExpressionBuilder {
        // For value object values, need to map them
        // For primitive types, direct assignment
        if (valueType is SimpleValueObjectApiType || valueType is ComplexValueObjectApiType) {
            return mapOp(variable).mapValues { keyVar, valueVar ->
                valueType.modernDeserialize(context, valueVar)
            }
        }
        return variable
    }

    override fun modernSerialize(
        context: ModernSerializeContext,
        variable: ExpressionBuilder
    ): ExpressionBuilder {
        // For value object values, need to serialize them
        if (valueType is SimpleValueObjectApiType || valueType is ComplexValueObjectApiType) {
            return mapOp(variable).mapValues { keyVar, valueVar ->
                valueType.modernSerialize(context, valueVar)
            }
        }
        return variable
    }

    override fun getExample(): ExpressionBuilder {
        return mapOf(keyType.getExample() to valueType.getExample())
    }
}
```

### 4. Update ApiTypeFactoryLogic
**File**: `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypeFactoryLogic.kt`

Update `create()` method around lines 20-77 to handle MAP wrapper:

```kotlin
override fun create(type: TypeDefinition?): ApiTypeLogic {
    // ... existing null checks

    val isOptional = type.getWrappers().contains(TypeWrapper.OPTIONAL)
    val isList = type.getWrappers().contains(TypeWrapper.LIST)
    val isMap = type.getWrappers().contains(TypeWrapper.MAP)

    val apiType = when {
        isOptional -> OptionalApiType(create(withoutTypeWrapper(type, TypeWrapper.OPTIONAL)))
        isList -> ListApiType(create(withoutTypeWrapper(type, TypeWrapper.LIST)))
        isMap -> {
            // Extract key and value types from name: "MAP<KeyType,ValueType>"
            val typeName = type.getName()
            val mapPattern = Regex("""MAP<([^,]+),([^>]+)>""")
            val match = mapPattern.find(typeName)

            if (match != null) {
                val keyTypeName = match.groupValues[1].trim()
                val valueTypeName = match.groupValues[2].trim()

                val keyTypeDef = TypeDefinition.create(name = keyTypeName, wrappers = emptyList())
                val valueTypeDef = TypeDefinition.create(name = valueTypeName, wrappers = emptyList())

                MapApiType(create(keyTypeDef), create(valueTypeDef))
            } else {
                throw IllegalArgumentException("Invalid map type format: $typeName")
            }
        }
        // ... rest of existing logic for base types, value objects, etc.
    }

    return apiType
}

private fun withoutTypeWrapper(type: TypeDefinition, wrapper: TypeWrapper): TypeDefinition {
    return TypeDefinition.create(
        name = type.getName(),
        wrappers = type.getWrappers().filter { it != wrapper }
    )
}
```

### 5. Add Code Builder Map Support
**File**: `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/types/Map.kt` (NEW FILE)

Create new file for map type builders:

```kotlin
package com.github.bratek20.codebuilder.types

import com.github.bratek20.codebuilder.builders.ExpressionBuilder
import com.github.bratek20.codebuilder.builders.TypeBuilder
import com.github.bratek20.codebuilder.builders.expression
import com.github.bratek20.codebuilder.builders.typeName

fun mapType(keyType: TypeBuilder, valueType: TypeBuilder) = typeName { c ->
    c.lang.mapType(keyType.build(c), valueType.build(c))
}

fun mapOf(vararg pairs: Pair<ExpressionBuilder, ExpressionBuilder>): ExpressionBuilder {
    return expression { c ->
        c.lang.mapOf(pairs.map { (k, v) -> k.build(c) to v.build(c) })
    }
}

class MapOperations(
    private val variable: ExpressionBuilder
) {
    fun get(key: ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            c.lang.mapGet(variable.build(c), key.build(c))
        }
    }

    fun put(key: ExpressionBuilder, value: ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            c.lang.mapPut(variable.build(c), key.build(c), value.build(c))
        }
    }

    fun mapValues(transform: (ExpressionBuilder, ExpressionBuilder) -> ExpressionBuilder): ExpressionBuilder {
        return expression { c ->
            val keyParam = c.lang.lambdaParam("key")
            val valueParam = c.lang.lambdaParam("value")
            val keyExpr = expression { keyParam }
            val valueExpr = expression { valueParam }
            val transformResult = transform(keyExpr, valueExpr).build(c)

            c.lang.mapMapValues(variable.build(c), keyParam, valueParam, transformResult)
        }
    }

    fun size(): ExpressionBuilder {
        return expression { c ->
            c.lang.mapSize(variable.build(c))
        }
    }
}

fun mapOp(variable: ExpressionBuilder): MapOperations {
    return MapOperations(variable)
}
```

### 6. Add Language-Specific Map Methods
**File**: `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/core/Languages.kt`

Add methods to `CodeBuilderLanguage` interface (around line 40):

```kotlin
interface CodeBuilderLanguage {
    // ... existing methods

    // Map support
    fun mapType(keyType: String, valueType: String): String
    fun mapOf(pairs: List<Pair<String, String>>): String
    fun mapGet(mapVar: String, key: String): String
    fun mapPut(mapVar: String, key: String, value: String): String
    fun mapMapValues(mapVar: String, keyParam: String, valueParam: String, transform: String): String
    fun mapSize(mapVar: String): String
}
```

Implement for Kotlin (around line 85):

```kotlin
class Kotlin : CodeBuilderLanguage {
    // ... existing implementations

    override fun mapType(keyType: String, valueType: String): String {
        return "Map<$keyType, $valueType>"
    }

    override fun mapOf(pairs: List<Pair<String, String>>): String {
        if (pairs.isEmpty()) {
            return "emptyMap()"
        }
        val pairsStr = pairs.joinToString(", ") { (k, v) -> "$k to $v" }
        return "mapOf($pairsStr)"
    }

    override fun mapGet(mapVar: String, key: String): String {
        return "$mapVar[$key]"
    }

    override fun mapPut(mapVar: String, key: String, value: String): String {
        return "$mapVar[$key] = $value"
    }

    override fun mapMapValues(mapVar: String, keyParam: String, valueParam: String, transform: String): String {
        return "$mapVar.mapValues { (${keyParam}, ${valueParam}) -> $transform }"
    }

    override fun mapSize(mapVar: String): String {
        return "$mapVar.size"
    }
}
```

Stub for TypeScript (around line 271) and C# (around line 450):

```kotlin
// TypeScript - stub for now
override fun mapType(keyType: String, valueType: String): String {
    return "Map<$keyType, $valueType>"
}
// ... other methods with TODO comments

// C# - stub for now
override fun mapType(keyType: String, valueType: String): String {
    return "Dictionary<$keyType, $valueType>"
}
// ... other methods with TODO comments
```

### 7. Value Object Generation
**No changes needed** - The existing logic in `SerializableApiType.getClassOps()` should automatically handle map types through the serialization/deserialization methods.

Generated Kotlin code should look like:
```kotlin
data class SomeClass(
    private val myMap: Map<String, Int>,  // Map field
) {
    fun getMyMap(): Map<String, Int> {
        return this.myMap
    }

    companion object {
        fun create(myMap: Map<String, Int>): SomeClass {
            return SomeClass(myMap = myMap)
        }
    }
}
```

For maps with value object values:
```kotlin
data class SomeClass(
    private val idMap: Map<String, String>,  // Internal: String -> String
) {
    fun getIdMap(): Map<String, SomeId> {
        return this.idMap.mapValues { (_, value) -> SomeId(value) }  // Deserialize
    }

    companion object {
        fun create(idMap: Map<String, SomeId>): SomeClass {
            return SomeClass(
                idMap = idMap.mapValues { (_, value) -> value.value }  // Serialize
            )
        }
    }
}
```

### 8. Builder Pattern Support
**File**: Pattern for generated builders similar to lists

Generated code should look like:
```kotlin
data class SomeClassDef(
    var myMap: Map<String, Int> = emptyMap(),  // Direct map for primitives
)

data class SomeClass2Def(
    var idMap: Map<String, String> = emptyMap(),  // String keys for VO values
)

fun someClass(init: SomeClassDef.() -> Unit = {}): SomeClass {
    val def = SomeClassDef().apply(init)
    return SomeClass.create(myMap = def.myMap)
}

fun someClass2(init: SomeClass2Def.() -> Unit = {}): SomeClass2 {
    val def = SomeClass2Def().apply(init)
    return SomeClass2.create(
        idMap = def.idMap.mapValues { (_, v) -> SomeId(v) }  // Convert values
    )
}
```

### 9. Assert Pattern Support
**File**: Pattern for generated asserts similar to lists

Generated code should look like:
```kotlin
class SomeClassAssert(private val actual: SomeClass) {
    fun hasMyMap(expected: Map<String, Int>): SomeClassAssert {
        if (actual.getMyMap() != expected) {
            throw AssertionError("Expected myMap to be $expected but was ${actual.getMyMap()}")
        }
        return this
    }
}

class SomeClass2Assert(private val actual: SomeClass2) {
    fun hasIdMap(expected: Map<String, String>): SomeClass2Assert {
        val actualMap = actual.getIdMap().mapValues { (_, v) -> v.value }
        if (actualMap != expected) {
            throw AssertionError("Expected idMap to be $expected but was $actualMap")
        }
        return this
    }
}
```

### 10. Testing Strategy

1. **Create test module definition** in `example/hla/MapTestModule.module`:
```
ValueObjects
    MapId: string

    SimpleMapClass
        primitiveMap: <string, int>

    ComplexMapClass
        idMap: <string, MapId>

    OptionalMapClass
        optMap: <string, int>?
```

2. **Generate Kotlin code**:
```bash
cd example
java -jar ../app/build/libs/app.jar start hla kotlin MapTestModule
```

3. **Verify generated files**:
- Value objects compile
- Builders work
- Asserts work

4. **Write integration tests** in `example/tests/`:
```kotlin
@Test
fun testMapSupport() {
    val obj = simpleMapClass {
        primitiveMap = mapOf("key1" to 10, "key2" to 20)
    }

    assertSimpleMapClass(obj)
        .hasprimitiveMap(mapOf("key1" to 10, "key2" to 20))
}
```

## Key Design Decisions

1. **Map representation in TypeDefinition**: Use special naming convention `MAP<KeyType,ValueType>` to encode both types in the name field
2. **Wrapper order**: For optional maps, OPTIONAL comes before MAP (similar to optional lists)
3. **Serialization**: Maps with value object values need `.mapValues()` for conversion
4. **Builder pattern**: Use Kotlin's native `Map<K, V>` type in builder definitions
5. **Kotlin focus**: TypeScript and C# implementations are stubbed but not fully implemented

## Files to Create/Modify

### New Files
- `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/types/Map.kt`
- `example/hla/MapTestModule.module` (for testing)

### Modified Files
1. `lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/Enums.kt`
2. `lib/src/main/kotlin/com/github/bratek20/hla/parsing/impl/ModuleGroupParserLogic.kt`
3. `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypes.kt`
4. `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypeFactoryLogic.kt`
5. `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/core/Languages.kt`

## Potential Issues & Solutions

1. **Issue**: Map keys with value objects
   **Solution**: Phase 1 only supports primitive keys (string, int). Value object keys can be added later.

2. **Issue**: Nested maps `<string, <string, int>>`
   **Solution**: Should work automatically with recursive type parsing, but needs testing.

3. **Issue**: Import statements for Map type
   **Solution**: Kotlin's Map is in stdlib, should be auto-imported. Check if explicit imports needed.

## Success Criteria

- [ ] Parser recognizes `<Key, Value>` syntax
- [ ] Parser recognizes `<Key, Value>?` for optional maps
- [ ] Generated Kotlin value objects use `Map<K, V>`
- [ ] Maps with primitive values work
- [ ] Maps with value object values serialize/deserialize correctly
- [ ] Builders accept maps and convert values as needed
- [ ] Asserts compare maps correctly
- [ ] All existing tests still pass
- [ ] New integration tests pass for map functionality
