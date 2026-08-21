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

## ⚠️ CRITICAL: HLA is Self-Hosting

**The HLA project uses itself to generate its own code!**

### Important Rules for Modifying HLA's Own Definitions

1. **NEVER manually edit generated files in the HLA lib module** (e.g., `lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/ValueObjects.kt`)
   - These files are auto-generated from `.module` files
   - Manual edits will be overwritten when the module is regenerated

2. **Always modify the `.module` file instead:**
   - HLA's own module definitions are in `hla/` directory (not `example/hla/`)
   - For example, `HttpDefinition` is defined in `hla/Definitions.module`

3. **Use the update script to regenerate:**
   - Navigate to the `bash/` directory
   - Run `./updateModule.sh` to regenerate HLA's own modules
   - This ensures all generated code stays in sync

### Example: Adding a Field to HttpDefinition

**❌ WRONG:**
```kotlin
// Directly editing lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/ValueObjects.kt
data class HttpDefinition(
    private val requestResponseWrapping: Boolean?,  // Don't add here!
)
```

**✅ CORRECT:**
1. Edit `hla/Definitions.module` to add the field to HttpDefinition
2. Navigate to `bash/` directory
3. Run `./updateModule.sh` to regenerate
4. The ValueObjects.kt file will be automatically updated

### HLA Module Locations

- **HLA's own modules**: `hla/*.module` (the framework's internal definitions)
- **Example modules**: `example/hla/*.module` (for testing and examples)
- **Generated HLA code**: `lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/`
- **Update script**: `bash/updateModule.sh`

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

## Coding Style Guidelines

### Comments

- **Minimize comments in code** - Code should be self-explanatory through clear naming and structure
- **Only add comments when logic is not self-evident** - Complex algorithms, business rules, or non-obvious workarounds may need explanation
- **DO NOT add comments that just restate the code** - Bad: `// Create a map` above `val map = mutableMapOf()`
- **DO NOT add docstring comments or type annotations to code you didn't change** - Only document new functionality you add
- **Comments should explain "why", not "what"** - The code already shows what it does

### Imports

- **Always add explicit imports** - Never use fully qualified names in code (e.g., `com.github.bratek20.hla.queries.api.MapTypeParser.extractKeyValueTypes()`)
- **Use short, simple names with proper imports** - Write `MapTypeParser.extractKeyValueTypes()` with `import com.github.bratek20.hla.queries.api.MapTypeParser`
- **Group imports logically** - External dependencies first, then project imports
- **Use wildcard imports only when importing many items from same package** - Prefer explicit imports for clarity

**Good Example**:
```kotlin
import com.github.bratek20.hla.queries.api.MapTypeParser

val mapInfo = MapTypeParser.parseMapType(typeString)
```

**Bad Example**:
```kotlin
// No import, using fully qualified name
val mapInfo = com.github.bratek20.hla.queries.api.MapTypeParser.parseMapType(typeString)
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
    // C# only - Kotlin and TypeScript still go through valueObjects.vm
    return c.language.name() == ModuleLanguage.C_SHARP
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

## Orientation Map (read this before exploring)

This section exists to stop repeated whole-codebase exploration. It records the facts that
are expensive to rediscover. If something here turns out to be wrong, fix it here.

### The one funnel every generated file passes through

`PatternGenerator` (`lib/.../generation/impl/core/Generators.kt`) is the spine:

```
generatePatterns()
  ├─ getFiles() / getDirectory()      -> raw files, BYPASS the funnel (InitSql, Examples JSON)
  ├─ !supportsCodeBuilder()           -> generateFileContent()  [velocity]  ─┐
  └─ getOperations()/getOperationsPerFile() -> generateFileContent(ops) [cb] ─┤
                                                                             ↓
                                              generatePatternFile()  <- single funnel
                                              (adds the DO NOT EDIT header)
```

**Any cross-cutting transform of generated file text belongs in `generatePatternFile()`** —
it covers velocity and code builder patterns uniformly. That is where the modern TypeScript
ESM transform hooks in.

Key `PatternGenerator` extension points: `patternName()`, `mode()` (`ONLY_START` files get no
header and are skipped on update), `supportsCodeBuilder()` (default **false** = velocity),
`shouldGenerate()`, `doNotGenerateTypeScriptNamespace()`, `useImportsCalculator()`, `shouldSkip()`
(honours profile `onlyPatterns`/`skipPatterns`).

### Three separate "language" abstractions — don't confuse them

| Abstraction | Location | Purpose |
|---|---|---|
| `LanguageSupport` | `lib/.../generation/impl/core/language/` + `impl/languages/<lang>/<Lang>Support.kt` | per-language services: types, file extension, fixtures, `base()` |
| `CodeBuilderLanguage` | `code-builder/.../core/Languages.kt` (`Kotlin`, `TypeScript`, `CSharp`) | syntax level: keywords, list/map/optional forms, terminators |
| `LanguageTypes` | `lib/.../generation/impl/core/language/LanguageTypes.kt` + `<Lang>Types.kt` | string-template level, used by velocity and legacy paths |

`ModuleGeneratorLogic` is the **only** place a `ModuleLanguage` enum maps to a `LanguageSupport`.
`<Lang>Support.base()` is the only place a `CodeBuilderLanguage` is instantiated in production.
`CodeBuilderContext` carries only `lang` — no profile — so profile-driven behaviour either goes
through a constructor arg on the language (`TypeScript(modern)`) or stays in `lib/`.

### TypeScript: which patterns are code builder vs velocity

Code builder: `Enums`, `Exceptions`, `Events`, `Mocks`, `TestBase`, `PlayFabHandlers`,
`WebServerContext`, `Menu`, `Track`, `InitSql`, Examples.

Still velocity (`lib/src/main/resources/templates/type_script/`): `ValueObjects`, `DataClasses`,
`Interfaces`, `Builders`, `Diffs`, `Asserts`, `Logic`, `ImplContext`, `WebCommon`, `WebClient`,
`WebServer`, `WebClientContext`, `PropertyKeys`/`DataKeys`, `CustomTypes*`, `ImplTest`.

Several `.vm` files declare their own `namespace` (`keys`, `customTypesMapper`, `builders`,
`asserts`, `diffs`, `mocks`, `logic`, `implContext`, `implTest`, `webClientContext`); the rest get
wrapped generically in `Generators.kt`. `ImplGenerator.kt` additionally does raw string surgery to
inject `namespace <M>.Impl {`. Templates receive `moduleName` and `modern` via `contentBuilder()`.

### File and directory naming

- File name = `PatternName` verbatim + extension. **Only exception**: `Events` → `Notifications.ts`
  (`EventsGenerator.getOperationsPerFile`). `getOperationsPerFile` can set arbitrary names.
- `calcModuleDirectoryName` / `calcSubmoduleDirectoryName` / `HlaSrcPaths.getPathForSubmodule`
  all live in `lib/.../writing/impl/ModuleWriterLogic.kt` as top-level functions. Reuse them;
  do not reimplement layout rules. Kotlin lowercases, TS/C# keep PascalCase.
- Relative ESM specifier math: `writing/impl/RelativePaths.kt`.

### TypesWorld — what it does and does NOT know

`TypesWorldApi` (`lib/.../typesworld/`) is the type registry; `HlaTypePath` encodes
`[groups/]<Module>/<Submodule>/<Pattern>`; `ImportsCalculator` derives Kotlin/C# imports from it.

Hard limits worth knowing before designing anything on top of it:

- **Populated submodules**: `Api`, `ViewModel`, `View`, and `Impl` (Track only). `Web`, `Context`,
  `Fixtures`, `Tests`, `Examples`, `Menu`, `Prefabs` are **absent**.
- **Populated Api patterns**: `ValueObjects`, `DataClasses`, `CustomTypes`, `Events`, `Enums`
  (name only). `Interfaces`, `Exceptions`, `PropertyKeys`, `DataKeys`, `CustomTypesMapper`,
  `SerializedCustomTypes` are **absent**.
- **Dependencies come only from structure**: `extends` + field types + type arguments, one hop of
  indirection. Method signatures, function bodies and const initializers are invisible.
- `ensureType` enforces **global name uniqueness** and throws `SameNameTypeExistsException`. Do not
  register speculative or duplicated names.
- `mapToImport` **drops the last path segment**, so it cannot produce a file-level specifier.
- Populators live in `lib/.../hlatypesworld/impl/`, registered in `hlatypesworld/context/Impl.kt`.

Because of these gaps, modern TypeScript imports use TypesWorld for api types plus a lib-internal
`ModernTypeScriptExports` registry for everything else. See "Modern TypeScript" below.

### Parsing a `.module` file

`lib/.../parsing/impl/ModuleGroupParserLogic.kt`:
- Adding a root section requires adding its name to `knownRootSections` or parsing throws
  `UnknownRootSectionException`.
- `findSection(elements, "Name")` finds a section; `parseOptVariable(elements, "key")` reads a
  `key = value` assignment. Follow `parseKotlinConfig` / `parseTypeScriptConfig` as the pattern.

### Profiles (`properties.yaml`)

Deserialized by the external b20 lib with `FAIL_ON_UNKNOWN_PROPERTIES` disabled — **unknown keys
are silently dropped**. A config key that "does nothing" usually means the field is missing from
`hla/Facade.module`, not that the code ignores it.

`FilesModifiers` (`lib/.../writing/impl/`) maintains the shared files (tsconfig, package.json,
launch.json, entry). It is skipped entirely when `onlyUpdate` is true, so `update`/`updateAll`
never touch them. Each file is maintained only if the profile declares its path.

### Self-hosting: editing HLA's own definitions

1. Edit `hla/*.module`.
2. Run `bash/updateModule.sh` (needs a built app) to regenerate.
3. If you cannot build, hand-mirror into the generated files so the branch compiles — the update
   script must then produce identical output. For a facade VO that is three files:
   `lib/src/main/.../facade/api/ValueObjects.kt`,
   `lib/src/testFixtures/.../facade/fixtures/Builders.kt` and `.../fixtures/Diffs.kt`.

Generated shapes to copy exactly:

| `.module` | ValueObjects.kt | Builders.kt | Diffs.kt |
|---|---|---|---|
| `x: bool?` | `private val x: Boolean?` + `getX(): Boolean?` | `var x: Boolean? = null` | `xEmpty` + `x`, using `getX()!!` |
| `x: Path? = empty` | `private val x: String? = null` + `getX(): Path? { return this.x?.let { pathCreate(it) } }` | `var x: String? = null` → `x?.let { pathCreate(it) }` | `xEmpty` + `x`, using `getX()!!` |
| `x: SomeVo? = empty` | `private val x: SomeVo? = null` | `var x: (SomeVoDef.() -> Unit)? = null` | `xEmpty` + `x: (ExpectedSomeVo.() -> Unit)?` |

Adding a field with `= empty` keeps `create()` backward compatible; without a default it becomes a
required parameter and breaks callers.

### Testing

- `HlaFacadeTest` is a **golden-file** test: it generates into `example/<lang>/` and compares.
  Any output change means regenerating the examples or the test fails. It also runs
  `FilesModifiers`, so it **mutates committed** tsconfig/package.json/launch.json/entry files.
- `ModuleGroupParserTest` + `lib/src/test/resources/parsing/<case>/` for parser changes.
- Prefer extracting pure `List<String> -> List<String>` helpers for file-text editing so they can
  be tested without a `Files` fake (see `writing/impl/ModernFileEdits.kt`).

## Modern TypeScript (ESM) generation

Legacy TS output is global-scope: no imports/exports, `namespace` blocks, ordered tsconfig `files`
list. Modern output is real ES modules. Reference implementation to match:
`../../Rortos/aircombatcs/Modern/Src/DeepLinks` and `Modern/Test/DeepLinks`.

### Opting in — two flags, ANDed

```
# profile in properties.yaml            # section in the .module file
typeScript:                             TypeScript
  modern: true                              modern = true
  entryPath: "main/entry.ts"
  vitestConfigPath: "./vitest.config.mts"
```

`isModernModule(profile, module)` in `generation/impl/languages/typescript/Modern.kt`. Both are
required: the same `.module` files are generated by a legacy and a modern profile side by side, and
one modern profile normally holds a mix of migrated and legacy modules.

### Files

| File | Role |
|---|---|
| `Modern.kt` | the two flags and `isModernModule` |
| `ModernTypeScriptSource.kt` | namespace unwrap, `export` insertion, declared-name scan |
| `ModernTypeScriptPaths.kt` | `HlaTypePath` → relative specifier |
| `ModernTypeScriptExports.kt` | name → file registry for what TypesWorld does not model |
| `ModernTypeScriptTransformer.kt` | orchestration: rewrite references, emit imports |
| `writing/impl/ModernFileEdits.kt` | pure edits for entry.ts, package.json, launch.json |

### Rules that are load-bearing

- **Import only from modern modules.** `queries.group.getModules().filter { it.isMarkedModern() }`.
  Legacy modules — same group or pulled in via `imports` — keep their `Module.X.y` global form.
  Cross-group modern→modern is not resolved (would need cross-project paths).
- **`ImplContext` must be `export const Api = { … }`**, a mutable object. `Mocks.ts` assigns to
  `Api.someMethod`, which is illegal on an ESM namespace import.
- **`Diffs.ts` must hoist the narrowed optional** into a local before `forEach`; strict TS cannot
  keep `expected.x !== undefined` alive across the callback (`ExpectedTypeField.localName()`).
- **`TestBase` must not export `test`** in modern mode — vitest provides it.
- Ambient legacy globals (`Optional`, `STRING`, `Class()`, `HandlerContext`, `StringEnumClass`,
  `Undefined`, `AssertEquals`, `DependencyName`, `Ts.E2E`) are **never** imported.
- `= STRING` / `= Class(X)` field initializers are runtime descriptors reflected on by
  `ObjectCreation` — they must survive, hence `useDefineForClassFields: false` in the consumer.
- Builder and assert function names are the camelCase of a structure name, so they collide with
  that structure's own field and parameter names. They are registered **qualified-only**; same for
  the `c` context variable. Over-registering bare names produces spurious imports.

## Working with the Project

### ⚠️ Do not build, generate or test unless explicitly asked

Claude must **not** run `./gradlew` builds, `./gradlew test`, `updateModule.sh` or example
regeneration on its own. Make the code change, then tell the user what to build, regenerate and
test — the user runs it and reports back. Only run these commands when the user asks for it in
that message.

The command sections below are reference for what to tell the user (or to run when asked).

### Building the App

```bash
./gradlew :app:build
```

The JAR is created at: `app/build/libs/app.jar`

### Regenerating HLA's Own Modules

**IMPORTANT**: HLA uses itself to generate its own code. When you modify HLA's internal `.module` files (in `hla/` directory), use the update script:

```bash
cd bash
./updateModule.sh
```

This regenerates HLA's own definitions (like `HttpDefinition`, `ModuleDefinition`, etc.) located in:
- `lib/src/main/kotlin/com/github/bratek20/hla/definitions/api/`
- `lib/src/testFixtures/kotlin/com/github/bratek20/hla/definitions/fixtures/`

**Never manually edit these generated files!** The flow is:
1. Modify the `.module` file in `hla/` directory
2. `bash/updateModule.sh`
3. Rebuild the app: `./gradlew :app:build`

Steps 2 and 3 are run by the user unless they explicitly ask Claude to run them.

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

**⚠️ CRITICAL: When modifying SomeModule.module**

When `example/hla/SomeModule.module` changes, it **MUST** be regenerated for **ALL** languages and profiles that use it to avoid test failures:

```bash
cd example

# Regenerate for all languages
java -jar ../app/build/libs/app.jar update hla kotlin SomeModule
java -jar ../app/build/libs/app.jar update hla typeScript SomeModule
java -jar ../app/build/libs/app.jar update hla cSharp SomeModule
java -jar ../app/build/libs/app.jar update hla kotlinSkipPatterns SomeModule
```

**Why this is required:**
- The `HlaFacadeTest` integration tests compare generated output with example files
- Example files exist for multiple language profiles: `kotlin`, `typeScript`, `cSharp`, `kotlinSkipPatterns`
- If you only regenerate one language, other tests will fail because they detect differences
- Look in `.run/` folder for common configurations - you can use "StartAll" configurations for each profile

**Quick tip**: Use `startAll` or `updateAll` operations to regenerate all modules at once for a specific profile.

### Running Tests

Run by the user, not by Claude (see the rule at the top of this section).

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

### Map Types in Method Arguments

Optional map types like `<string, string>?` are fully supported in method arguments:

```
Interfaces
    SomeInterface
        methodWithOptionalMap(optMap: <string, string>?): <string, string>?
```

This generates correct code in all languages:
- **Kotlin**: `fun methodWithOptionalMap(optMap: Map<String, String>?): Map<String, String>?`
- **TypeScript**: `methodWithOptionalMap(optMap: Optional<Map<string, string>>): Optional<Map<string, string>>`
- **C#**: `Dictionary<string, string>? MethodWithOptionalMap(Dictionary<string, string>? optMap)`

The parser uses `splitNotInsideAngleBrackets()` to correctly handle commas inside angle brackets.

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

## Useful Commands

Reference only — Claude suggests these, the user runs them (unless explicitly asked to run them).

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
