# HLA Project - Claude Context Documentation

This document contains important context and learnings about the HLA (High Level Architecture) code generation project.

## Project Overview

HLA is a code generation framework that converts `.module` definition files into type-safe code for multiple languages (Kotlin, TypeScript, C#). It generates:
- Value Objects (simple and complex)
- Data Classes
- Interfaces
- Enums
- Test Fixtures (Builders, Asserts, Mocks)
- Web clients/servers

## Architecture

### Key Components

1. **Code Builder** (`code-builder/` module)
   - Language-agnostic DSL for generating code
   - Supports Kotlin, TypeScript, C#
   - Uses builder pattern with typed DSL

2. **HLA Library** (`lib/` module)
   - Core generation logic
   - Pattern generators (ValueObjects, DataClasses, Interfaces, etc.)
   - API type system

3. **HLA App** (`app/` module)
   - CLI tool for running code generation
   - Entry point: `app/src/main/kotlin/com/github/bratek20/hla/app/Main.kt`

4. **Examples** (`example/` directory)
   - Contains `.module` definition files in `example/hla/`
   - Generated code in `example/kotlin/`, `example/typescript/`, `example/c-sharp/`
   - Test suite in `example/tests/`
   - `example/hla/properties.yaml` - Configuration for generation profiles

### Generation Flow

```
.module file → Parser → Module Definition → Pattern Generators → Code Builder → Language-specific Code
```

## Code Builder - Best Practices

### ⚠️ CRITICAL RULES

1. **NEVER use `hardcodedExpression()` when proper builders exist**
   - Always extend code builder with new functionality
   - Use typed builders: `plus{}`, `minus{}`, `times{}`, `methodCall{}`, `constructorCall{}`, etc.
   - Example: Use `plus { left = ...; right = ... }` instead of `hardcodedExpression("a + b")`

2. **Language-specific behavior is handled by the language backend**
   - TypeScript automatically adds `new` to constructor calls via `constructorCall(className)`
   - Kotlin uses `operator` keyword via `MethodBuilder.operator = true`
   - C# extends `ValueObject` base class

3. **Check existing builders before creating new ones**
   - Look in `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/builders/`
   - Common builders: `ClassBuilder`, `MethodBuilder`, `FieldBuilder`, `ConstructorCallBuilder`

### Adding New Expression Builders

When you need arithmetic or other operations:

**File**: `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/builders/SimpleExpressionBuilders.kt`

```kotlin
class MinusBuilder: ExpressionBuilder {
    lateinit var left: ExpressionBuilder
    lateinit var right: ExpressionBuilder

    override fun build(c: CodeBuilderContext): String {
        return "${left.build(c)} - ${right.build(c)}"
    }
}
typealias MinusBuilderOps = MinusBuilder.() -> Unit
fun minus(ops: MinusBuilderOps): MinusBuilder {
    return MinusBuilder().apply(ops)
}
```

## Value Objects Generation (Recent Refactoring)

### Migration from Velocity to Code Builder

**Completed**: Migrated all languages from Velocity templates to code builder approach.

**Deleted files**:
- `lib/src/main/resources/templates/kotlin/api/valueObjects.vm`
- `lib/src/main/resources/templates/type_script/api/valueObjects.vm`

### Implementation Location

**File**: `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypes.kt`

#### SimpleValueObjectApiType

Handles simple value objects (wrapping a single value like `SomeId: string`).

Key method: `getClassOps(): ClassBuilderOps`

**Kotlin generation**:
```kotlin
c.lang is Kotlin -> {
    dataClass = true  // Generates data class

    addField { ... }  // Constructor field

    addMethod {
        overridesClassMethod = true
        name = "toString"
        ...
    }

    // For Int/Long types, add arithmetic operators
    if (boxedType.name == BaseType.INT || boxedType.name == BaseType.LONG) {
        addKotlinArithmeticOperators()
    }
}
```

**TypeScript generation**:
```kotlin
c.lang is TypeScript -> {
    addField {
        name = "value${this@SimpleValueObjectApiType.name}"  // TypeScript naming convention
        fromConstructor = true
    }

    addMethod { name = "getValue"; ... }
    addMethod { name = "equals"; ... }
    addMethod { name = "toString"; ... }

    // For number types, add valueOf() and arithmetic methods
    if (boxedType.name == BaseType.INT || boxedType.name == BaseType.LONG) {
        addTypeScriptNumericMethods()
    }
}
```

#### ComplexValueObjectApiType

Extends `SerializableApiType` which handles complex value objects (multiple fields).

**Key**: Set `dataClass = true` for Kotlin in `SerializableApiType.getClassOps()`:
```kotlin
if (c.lang is Kotlin) {
    dataClass = true
}
```

### Pattern Generator

**File**: `lib/src/main/kotlin/com/github/bratek20/hla/generation/impl/core/api/patterns/ValueObjectsGenerator.kt`

```kotlin
override fun supportsCodeBuilder(): Boolean {
    return true  // All languages now use code builder
}

override fun getOperations(): TopLevelCodeBuilderOps = {
    val simpleVOs = module.getSimpleValueObjects().map {
        apiTypeFactory.create<SimpleValueObjectApiType>(it)
    }
    val complexVOs = modules.getComplexValueObjects(module).map {
        apiTypeFactory.create<ComplexValueObjectApiType>(it)
    }
    simpleVOs.forEach { addClass(it.getClassOps()) }
    complexVOs.forEach { addClass(it.getClassOps()) }
}
```

## Working with the Project

### Building the App

```bash
./gradlew :app:build
```

The JAR is created at: `app/build/libs/app.jar`

### Regenerating Examples

```bash
cd example
java -jar ../app/build/libs/app.jar update hla kotlin SomeModule
java -jar ../app/build/libs/app.jar update hla typeScript SomeUserModule
java -jar ../app/build/libs/app.jar update hla cSharp SomeModule
```

**Format**: `<operation> <hla-folder> <profile> <module-name>`

Operations:
- `start` - Generate module for first time
- `update` - Regenerate existing module
- `updateAll` - Regenerate all modules in profile
- `startAll` - Generate all modules in profile

### Running Tests

```bash
# HLA library tests
./gradlew :lib:test

# Example project tests (uses generated code)
./gradlew :example:tests:test

# Specific test
./gradlew :lib:test --tests "*HlaFacadeTest*"
```

## Important Files

### Generation

- `lib/src/main/kotlin/com/github/bratek20/hla/generation/impl/core/api/patterns/` - Pattern generators
- `lib/src/main/kotlin/com/github/bratek20/hla/apitypes/impl/ApiTypes.kt` - Type system and code generation logic
- `lib/src/main/kotlin/com/github/bratek20/hla/generation/impl/core/PatternGenerator.kt` - Base class for generators

### Code Builder

- `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/builders/ClassBuilder.kt` - Class generation
- `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/builders/ProcedureBuilders.kt` - Method/function builders
- `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/builders/SimpleExpressionBuilders.kt` - Expression builders
- `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/core/Languages.kt` - Language-specific behavior

### Testing

- `lib/src/test/kotlin/com/github/bratek20/hla/facade/HlaFacadeTest.kt` - Main integration tests
- `example/tests/src/test/kotlin/` - Tests using generated code

## Module Definition Format

Example `.module` file:

```
Enums
    Status
        ACTIVE
        INACTIVE

ValueObjects
    UserId: string
    Age: int

    UserProfile
        name: string
        age: Age
        status: Status

Interfaces
    UserRepository
        findUser(id: UserId): UserProfile?
        saveUser(profile: UserProfile)
```

## Generated Code Patterns

### Kotlin

**Simple VO**:
```kotlin
data class UserId(
    val value: String
) {
    override fun toString(): String {
        return value.toString()
    }
}
```

**With arithmetic operators** (Int/Long):
```kotlin
data class Age(
    val value: Int
) {
    override fun toString(): String { ... }
    operator fun plus(other: Age): Age { ... }
    operator fun minus(other: Age): Age { ... }
    operator fun times(amount: Int): Age { ... }
}
```

**Complex VO**:
```kotlin
data class UserProfile(
    private val name: String,
    private val age: Int
) {
    fun getName(): String { return name }
    fun getAge(): Age { return Age(age) }

    companion object {
        fun create(name: String, age: Age): UserProfile {
            return UserProfile(name, age.value)
        }
    }
}
```

### TypeScript

**Simple VO**:
```typescript
class UserId {
    constructor(
        private readonly valueUserId: string
    ) {}

    getValue(): string { return this.valueUserId }
    equals(other: UserId): boolean { ... }
    toString(): string { ... }
}
```

**With numeric methods** (number type):
```typescript
class Age {
    constructor(private readonly valueAge: number) {}

    getValue(): number { ... }
    valueOf(): number { ... }
    plus(other: Age): Age { ... }
    minus(other: Age): Age { ... }
    times(amount: number): Age { ... }
}
```

**Complex VO**:
```typescript
class UserProfile {
    private name = STRING
    private age = NUMBER

    static create(name: string, age: Age): UserProfile { ... }
    static createNamed({name, age}: {name: string; age: Age}): UserProfile { ... }

    getName(): string { ... }
    getAge(): Age { ... }
}
```

## Configuration

### properties.yaml

Located at: `example/hla/properties.yaml`

Defines profiles for code generation:

```yaml
profiles:
  - name: "kotlin"
    language: "KOTLIN"
    paths:
      project: "../kotlin"
      src:
        default: "src/main/kotlin/com/some/pkg"
        overrides:
          - submodule: "Tests"
            path: "src/test/kotlin/com/some/pkg"
          - submodule: "Fixtures"
            path: "src/testFixtures/kotlin/com/some/pkg"
```

### tmp Directory

`example/tmp/` - Contains generated code output during test runs. Can be configured for debugging specific module/language combinations.

## Common Issues and Solutions

### Import Issues

Generated files sometimes have incorrect imports like:
```kotlin
import OtherModule.Api  // Wrong
```

Should be:
```kotlin
import com.some.pkg.othermodule.api.*  // Correct
```

**You can manually fix these** - the user mentioned generated files in examples can be tweaked for imports and formatting.

### Type Checking in Code Builder

Always use enum comparison for BaseType:
```kotlin
// Correct
if (boxedType.name == BaseType.INT || boxedType.name == BaseType.LONG)

// Wrong
if (boxedType.name.toString() == "Int")
```

### Adding Language-Specific Features

Use `when` blocks in `getClassOps()`:
```kotlin
when {
    c.lang is Kotlin -> { /* Kotlin-specific */ }
    c.lang is TypeScript -> { /* TypeScript-specific */ }
    c.lang is CSharp -> { /* C#-specific */ }
    else -> { /* Fallback */ }
}
```

## Testing Strategy

1. **Unit tests** - Test generation logic in isolation
2. **Integration tests** - `HlaFacadeTest` compares generated output with example files
3. **Functional tests** - `example/tests/` uses generated code to verify it compiles and works

### Test Expectations

- Tests allow **formatting differences** (whitespace, import order)
- Tests require **functional equivalence** (same class structure, methods, behavior)
- Example tests must **compile and pass** - this is the ultimate verification

## Extending the System

### Adding a New Pattern Generator

1. Create class extending `PatternGenerator` in `lib/src/main/kotlin/com/github/bratek20/hla/generation/impl/core/api/patterns/`
2. Override methods:
   - `patternName()` - Return `PatternName.YourPattern`
   - `shouldGenerate()` - When to generate this pattern
   - `supportsCodeBuilder()` - Return true for code builder approach
   - `getOperations()` - Return code builder operations
3. Register in module generator

### Adding Support for a New Language

1. Create language class in `code-builder/src/main/kotlin/com/github/bratek20/codebuilder/core/Languages.kt`
2. Implement `CodeBuilderLanguage` interface
3. Add language-specific cases in `ApiTypes.kt` methods
4. Update pattern generators' `getClassOps()` methods
5. Add profile in `properties.yaml`

## Best Practices

1. ✅ **Use code builder over templates** - Type-safe, testable, maintainable
2. ✅ **Extend builders for new operations** - Don't use hardcoded expressions
3. ✅ **Let language backend handle syntax** - Use `c.lang.constructorCall()`, etc.
4. ✅ **Test with actual compilation** - Example tests are critical
5. ✅ **Follow existing patterns** - Look at similar generators before implementing
6. ✅ **Use when blocks for language-specific logic** - Clean and maintainable
7. ✅ **Check BaseType with enum comparison** - Not string comparison

## Recent Changes (2026-02-11)

- ✅ Migrated Kotlin value objects from Velocity to code builder
- ✅ Migrated TypeScript value objects from Velocity to code builder
- ✅ Added `operator` flag to MethodBuilder for Kotlin operators
- ✅ Added `minus()` and `times()` builders to SimpleExpressionBuilders
- ✅ Deleted both Velocity templates (`valueObjects.vm` for Kotlin and TypeScript)
- ✅ All languages now use unified code builder approach

## Useful Commands

```bash
# Compile without tests
./gradlew :lib:assemble :app:build

# Compile and run all tests
./gradlew :lib:build

# Run specific test pattern
./gradlew :lib:test --tests "*ValueObjects*"

# Regenerate all Kotlin examples
cd example
for module in SomeModule OtherModule SimpleModule; do
  java -jar ../app/build/libs/app.jar update hla kotlin $module
done
```

## Project Structure

```
hla/
├── code-builder/          # Language-agnostic code generation DSL
│   └── src/main/kotlin/com/github/bratek20/codebuilder/
│       ├── builders/      # ClassBuilder, MethodBuilder, etc.
│       ├── core/          # Languages, BaseType, Context
│       └── types/         # TypeBuilder implementations
├── lib/                   # HLA core library
│   ├── src/main/kotlin/com/github/bratek20/hla/
│   │   ├── apitypes/      # Type system and generation logic
│   │   ├── generation/    # Pattern generators
│   │   ├── parsing/       # .module file parser
│   │   └── facade/        # Public API
│   └── src/test/kotlin/   # Unit and integration tests
├── app/                   # CLI application
│   └── src/main/kotlin/com/github/bratek20/hla/app/
├── example/               # Example project
│   ├── hla/              # .module definitions
│   ├── kotlin/           # Generated Kotlin code
│   ├── typescript/       # Generated TypeScript code
│   ├── c-sharp/          # Generated C# code
│   ├── tests/            # Tests using generated code
│   └── tmp/              # Temporary generation output
└── CLAUDE.md            # This file
```

## Additional Notes

- The project uses Gradle with Kotlin DSL
- Generated files have header: `// DO NOT EDIT! Autogenerated by HLA tool`
- Module definitions support: ValueObjects, DataClasses, Enums, Interfaces, CustomTypes, Events, Properties
- Test fixtures (Builders, Asserts, Mocks) are auto-generated for testing
- The codebase follows a builder pattern extensively - study existing builders before creating new ones
