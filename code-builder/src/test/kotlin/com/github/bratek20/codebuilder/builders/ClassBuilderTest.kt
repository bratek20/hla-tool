package com.github.bratek20.codebuilder.builders

import com.github.bratek20.codebuilder.core.*
import com.github.bratek20.codebuilder.types.baseType
import com.github.bratek20.codebuilder.types.typeName
import org.junit.jupiter.api.Test

class ClassBuilderTest {

    @Test
    fun `empty class`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass {
                    }
                """
            }
        }
    }

    @Test
    fun `class extension`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    extends {
                        className = "SomeParent"
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass: SomeParent {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass extends SomeParent {
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass: SomeParent {
                    }
                """
            }
        }
    }

    @Test
    fun `static field`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    addField {
                        type = typeName("OtherClass")
                        name = "someField"
                        static = true
                        value = constructorCall {
                            className = "OtherClass"
                            addArg {
                                string("SomeStr")
                            }
                        }
                    }
                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        private static readonly someField: OtherClass = new OtherClass("SomeStr")
                    }
                """
            }
        }
    }

    @Test
    fun `class with empty body that implements interface`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    implements = "SomeInterface"
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass: SomeInterface {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass implements SomeInterface {
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass: SomeInterface {
                    }
                """
            }
        }
    }

    @Test
    fun `class with comment and method`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"

                    addMethod {
                        comment = "some comment"
                        name = "someMethod"
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        // some comment
                        fun someMethod() {
                        }
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        // some comment
                        someMethod() {
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `class with fields`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"

                    addField {
                        name = "a"
                        type = typeName("A")
                        value = variable("null")
                    }
                    addField {
                        modifier = AccessModifier.PUBLIC
                        name = "b"
                        type = typeName("B")
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        private val a: A = null
                        val b: B
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        private readonly a: A = null
                        readonly b: B
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass {
                        readonly A a = null;
                        public readonly B b;
                    }
                """
            }
        }
    }

    @Test
    fun `constructor - field, arg and body`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    addField {
                        modifier = AccessModifier.PRIVATE
                        name = "idField"
                        type = baseType(BaseType.STRING)
                        fromConstructor = true
                    }
                    constructor {
                        addArg {
                            name = "idArg"
                            type = baseType(BaseType.STRING)
                        }
                        setBody {
                            add(comment {
                                "some comment"
                            })
                        }
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass(
                        private val idField: String,
                        idArg: String
                    ) {
                        init {
                            // some comment
                        }
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        constructor(
                            private readonly idField: string,
                            idArg: string
                        ) {
                            // some comment
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `static methods`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    addMethod {
                        static = true
                        name = "someMethod"
                    }
                    addMethod {
                        static = true
                        name = "otherMethod"
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass {
                        companion object {
                            fun someMethod() {
                            }
                            fun otherMethod() {
                            }
                        }
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass {
                        static someMethod() {
                        }
                        static otherMethod() {
                        }
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass {
                        public static void SomeMethod() {
                        }
                        public static void OtherMethod() {
                        }
                    }
                """
            }
        }
    }

    @Test
    fun constructorCall() {
        testOp {
            op = {
                add(constructorCall {
                    className = "SomeClass"
                })
            }
            langExpected {
                lang = Kotlin()
                expected = "SomeClass()"
            }
            langExpected {
                lang = TypeScript()
                expected = "new SomeClass()"
            }
        }
    }
    @Test
    fun complicatedClass() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeInterfaceSomeCommandRequest"

                    addField {
                        modifier = AccessModifier.PRIVATE
                        name = "id"
                        type = baseType(BaseType.STRING)
                        fromConstructor = true
                    }
                    addField {
                        modifier = AccessModifier.PRIVATE
                        name = "amount"
                        type = baseType(BaseType.INT)
                        fromConstructor = true
                    }

                    addMethod {
                        name = "getId"
                        returnType = typeName("SomeId")
                        setBody {
                            add(returnStatement {
                                constructorCall {
                                    className = "SomeId"
                                    addArg {
                                        instanceVariable("id")
                                    }
                                }
                            })
                        }
                    }
                    addMethod {
                        name = "getAmount"
                        returnType = baseType(BaseType.INT)
                        setBody {
                            add(returnStatement {
                                instanceVariable("amount")
                            })
                        }
                    }
                    addMethod {
                        static = true
                        name = "create"
                        returnType = typeName("SomeInterfaceSomeCommandRequest")
                        addArg {
                            type = typeName("SomeId")
                            name = "id"
                        }
                        addArg {
                            type = baseType(BaseType.INT)
                            name = "amount"
                        }
                        setBody {
                            add(returnStatement {
                                constructorCall {
                                    className = "SomeInterfaceSomeCommandRequest"
                                    addArg {
                                        getterFieldAccess {
                                            variableName = "id"
                                            fieldName = "value"
                                        }
                                    }
                                    addArg {
                                        variable("amount")
                                    }
                                }
                            })
                        }
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeInterfaceSomeCommandRequest(
                        private val id: String,
                        private val amount: Int
                    ) {
                        fun getId(): SomeId {
                            return SomeId(id)
                        }
                        fun getAmount(): Int {
                            return amount
                        }
                        companion object {
                            fun create(id: SomeId, amount: Int): SomeInterfaceSomeCommandRequest {
                                return SomeInterfaceSomeCommandRequest(id.value, amount)
                            }
                        }
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeInterfaceSomeCommandRequest {
                        constructor(
                            private readonly id: string,
                            private readonly amount: number
                        ) {}
                        getId(): SomeId {
                            return new SomeId(this.id)
                        }
                        getAmount(): number {
                            return this.amount
                        }
                        static create(id: SomeId, amount: number): SomeInterfaceSomeCommandRequest {
                            return new SomeInterfaceSomeCommandRequest(id.value, amount)
                        }
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeInterfaceSomeCommandRequest {
                        readonly string id;
                        readonly int amount;
                    
                        public SomeInterfaceSomeCommandRequest(
                            string id,
                            int amount
                        ) {
                            this.id = id;
                            this.amount = amount;
                        }
                        public SomeId GetId() {
                            return new SomeId(id);
                        }
                        public int GetAmount() {
                            return amount;
                        }
                        public static SomeInterfaceSomeCommandRequest Create(SomeId id, int amount) {
                            return new SomeInterfaceSomeCommandRequest(id.Value, amount);
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `extension with passing argument`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    extends {
                        className = "SomeParent"
                    }
                    constructor {
                        addArg {
                            name = "someArg"
                            type = typeName("SomeType")
                        }
                    }
                    addPassingArg("someArg")
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SomeClass(
                        someArg: SomeType
                    ): SomeParent(someArg) {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass extends SomeParent {
                        constructor(
                            someArg: SomeType
                        ) {
                            super(someArg)
                        }
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass: SomeParent {
                        public SomeClass(
                            SomeType someArg
                        ): base(someArg) {
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `extension with generic`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    extends {
                        className = "SomeParent"
                        generic = typeName("SomeType")
                    }
                })
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SomeClass extends SomeParent<SomeType> {
                    }
                """
            }
        }
    }

    @Test
    fun `field with deducted type`() {
        testOpValidation {
            language = Kotlin()
            op = {
                add(field {
                    name = "someField"
                })
            }
            errorMessage = "Field `someField` - type can not be deducted, value is required"
        }
        testOpValidation {
            language = Kotlin()
            op = {
                add(field {
                    name = "someField"
                    value = const(1)
                })
            }
            success = true
        }
        testOpValidation {
            language = CSharp()
            op = {
                add(field {
                    name = "someField"
                    value = const(1)
                })
            }
            errorMessage = "Field `someField` - type deduction not supported in C#"
        }
    }

    @Test
    fun `class with field getter`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    addField {
                        name = "someField"
                        type = baseType(BaseType.INT)
                        getter = true
                    }
                })
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass {
                        public int SomeField { get; }
                    }
                """
            }
        }
    }

    @Test
    fun `class with field set by constructor`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SomeClass"
                    addField {
                        name = "someField"
                        type = baseType(BaseType.INT)
                        fromConstructor = true
                    }
                    addField {
                        name = "otherField"
                        type = baseType(BaseType.INT)
                        fromConstructor = true
                    }
                })
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SomeClass {
                        readonly int someField;
                        readonly int otherField;
                    
                        public SomeClass(
                            int someField,
                            int otherField
                        ) {
                            this.someField = someField;
                            this.otherField = otherField;
                        }
                    }
                """
            }
        }
    }

    @Test
    fun `simple value object`() {
        testOp {
            op = {
                add(classBlock {
                    name = "SimpleValueObject"
                    addField {
                        name = "value"
                        type = baseType(BaseType.INT)
                        getter = true
                        fromConstructor = true
                    }
                })
            }
            langExpected {
                lang = Kotlin()
                expected = """
                    class SimpleValueObject(
                        val value: Int
                    ) {
                    }
                """
            }
            langExpected {
                lang = TypeScript()
                expected = """
                    class SimpleValueObject {
                        constructor(
                            readonly value: number
                        ) {}
                    }
                """
            }
            langExpected {
                lang = CSharp()
                expected = """
                    public class SimpleValueObject {
                        public int Value { get; }
                    
                        public SimpleValueObject(
                            int value
                        ) {
                            Value = value;
                        }
                    }
                """
            }
        }
    }
}