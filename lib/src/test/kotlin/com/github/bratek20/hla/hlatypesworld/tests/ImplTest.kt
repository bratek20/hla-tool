package com.github.bratek20.hla.hlatypesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.facade.HlaFacadeTest
import com.github.bratek20.hla.facade.api.ModuleName
import com.github.bratek20.hla.facade.api.ModuleOperationArgs
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.generation.impl.core.api.ApiTypeFactory
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpTypes
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.context.HlaTypesWorldImpl
import com.github.bratek20.hla.hlatypesworld.impl.HlaTypesWorldApiLogic
import com.github.bratek20.hla.parsing.api.ModuleGroupParser
import com.github.bratek20.hla.parsing.context.ParsingImpl
import com.github.bratek20.hla.queries.api.BaseModuleGroupQueries
import com.github.bratek20.hla.typesworld.api.TypesWorldApi
import com.github.bratek20.hla.typesworld.api.WorldType
import com.github.bratek20.hla.typesworld.api.WorldTypeName
import com.github.bratek20.hla.typesworld.context.TypesWorldImpl
import com.github.bratek20.hla.typesworld.fixtures.*
import com.github.bratek20.logs.LogsMocks
import com.github.bratek20.utils.directory.api.Path
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class HlaTypesWorldImplTest {
    @Test
    fun `should populate types`() {
        val c = someContextBuilder()
            .withModules(
                LogsMocks(),
                ParsingImpl(),
                TypesWorldImpl(),
                HlaTypesWorldImpl()
            ).build()

        val parser = c.get(ModuleGroupParser::class.java)
        val world = c.get(HlaTypesWorldApi::class.java)
        val typesWorldApi = c.get(TypesWorldApi::class.java)

        val moduleGroup = parser.parse(
            Path("../example/hla"),
            ProfileName("cSharp")
        )

        (world as HlaTypesWorldApiLogic).apiTypeFactory = ApiTypeFactory(
            BaseModuleGroupQueries(moduleGroup),
            CSharpTypes()
        )
        world.populate(moduleGroup)

        //then
        val assertHasTypeOfName = { typeName: String ->
            Assertions.assertThatCode {
                typesWorldApi.getTypeByName(WorldTypeName(typeName))
            }
                .withFailMessage("Type with name $typeName not found")
                .doesNotThrowAnyException()
        }
        val assertHasType = { typeName: String, typePath: String ->
            assertThat(typesWorldApi.hasType(worldType {
                name = typeName
                path = typePath
            }))
                .withFailMessage("Type $typeName not found")
                .isTrue()
        }

        val assertHasNotType = { typeName: String, typePath: String ->
            assertThat(typesWorldApi.hasType(worldType {
                name = typeName
                path = typePath
            }))
                .withFailMessage("Type $typeName found")
                .isFalse()
        }

        val assertHasClassType = { typeName: String, typePath: String, expectedClass: ExpectedWorldClassType.() -> Unit ->
            assertHasType(typeName, typePath)
            typesWorldApi.getClassType(WorldType(typeName, typePath)).let {
                assertWorldClassType(it, expectedClass)
            }
        }

        val assertHasConcreteParametrizedClass = { typeName: String, typePath: String, expectedClass: ExpectedWorldConcreteParametrizedClass.() -> Unit ->
            assertHasType(typeName, typePath)
            typesWorldApi.getConcreteParametrizedClass(WorldType(typeName, typePath)).let {
                assertWorldConcreteParametrizedClass(it, expectedClass)
            }
        }

        //special types
        assertHasType("int", "Language/Types/Api/Primitives")
        assertHasType("string", "Language/Types/Api/Primitives")

        assertHasType("List<int>", "Language/Types/Api/Primitives")
        assertHasType("Optional<int>", "Language/Types/Api/Primitives")

        //api types
        assertHasClassType("OtherClass", "OtherModule/Api/ValueObjects") {
            fields = listOf(
                {
                    name = "id"
                    type = {
                        name = "OtherId"
                    }
                },
                {
                    name = "amount"
                    type = {
                        name = "int"
                    }
                }
            )
        }

        assertHasClassType("ClassWithEnumList", "SomeModule/Api/ValueObjects") {
            fields = listOf {
                name = "enumList"
                type = {
                    name = "List<SomeEnum2>"
                }
            }
        }

        //b20 view model types
        assertHasType("EmptyModel", "B20/Frontend/UiElements/Api/ValueObjects")
        assertHasType("Label", "B20/Frontend/UiElements")

        assertHasNotType("Label", "OtherModule/ViewModel/GeneratedElements")

        //modules view model types
        assertHasConcreteParametrizedClass("UiElement<OtherClass>", "OtherModule/ViewModel/GeneratedElements") {
            typeArguments = listOf {
                name = "OtherClass"
            }
        }

        assertHasClassType("OtherClassVm", "OtherModule/ViewModel/GeneratedElements") {
            extends = {
                name = "UiElement<OtherClass>"
            }
            fields = listOf(
                {
                    name = "id"
                    type = {
                        name = "Label"
                        //path = "B20/Frontend/UiElements" //TODO-FIX
                    }
                },
                {
                    name = "amount"
                    type = {
                        name = "Label"
                    }
                }
            )
        }

        assertHasClassType("ClassWithEnumListVm", "SomeModule/ViewModel/GeneratedElements") {
            fields = listOf {
                name = "enumList"
                type = {
                    name = "SomeEnum2SwitchGroup"
                }
            }
        }
        assertHasType("SomeEnum2SwitchGroup", "SomeModule/ViewModel/GeneratedElements")
        assertHasType("SomeEnum2Switch", "SomeModule/ViewModel/GeneratedElements")
        assertHasClassType("OtherClassVmGroup", "OtherModule/ViewModel/GeneratedElements") {

        }
//        assertHasClassType("SomeEnum2SwitchGroup", "SomeModule/ViewModel/GeneratedElements") {
//            extends = {
//                name = "UiElementGroup<SomeEnum2Switch,SomeEnum>"
//            }
//        }

        //view types
        assertHasType("OtherClassView", "OtherModule/View/ElementsView")
    }

}