package com.github.bratek20.hla.hlatypesworld.tests

import com.github.bratek20.architecture.context.someContextBuilder
import com.github.bratek20.hla.facade.api.ProfileName
import com.github.bratek20.hla.apitypes.impl.ApiTypeFactoryLogic
import com.github.bratek20.hla.generation.impl.core.prefabs.CreationOrderCalculator
import com.github.bratek20.hla.generation.impl.languages.csharp.CSharpTypes
import com.github.bratek20.hla.hlatypesworld.api.HlaTypesWorldApi
import com.github.bratek20.hla.hlatypesworld.context.HlaTypesWorldImpl
import com.github.bratek20.hla.hlatypesworld.impl.HlaTypesWorldApiLogic
import com.github.bratek20.hla.mvvmtypesmappers.api.ViewModelToViewMapper
import com.github.bratek20.hla.mvvmtypesmappers.context.MvvmTypesMappersImpl
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
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class HlaTypesWorldImplTest {
    lateinit var typesWorldApi: TypesWorldApi
    lateinit var vmToViewMapper: ViewModelToViewMapper

    @BeforeEach
    fun setUp() {
        val c = someContextBuilder()
            .withModules(
                LogsMocks(),
                ParsingImpl(),
                TypesWorldImpl(),
                HlaTypesWorldImpl(),
                MvvmTypesMappersImpl()
            ).build()

        val parser = c.get(ModuleGroupParser::class.java)
        val world = c.get(HlaTypesWorldApi::class.java)
        typesWorldApi = c.get(TypesWorldApi::class.java)
        vmToViewMapper = c.get(ViewModelToViewMapper::class.java)

        val moduleGroup = parser.parse(
            Path("../example/hla"),
            ProfileName("cSharp")
        )

        (world as HlaTypesWorldApiLogic).apiTypeFactory = ApiTypeFactoryLogic(
            BaseModuleGroupQueries(moduleGroup),
            CSharpTypes()
        )
        world.populate(moduleGroup)
    }

    @Test
    fun `should populate special types`() {
        assertHasType("int", "Language/Types/Api/Primitives")
        assertHasType("string", "Language/Types/Api/Primitives")

        assertHasType("List<int>", "Language/Types/Api/Primitives")
        assertHasType("Optional<int>", "Language/Types/Api/Primitives")
    }


    @Test
    fun `should populate b20 view model types`() {
        assertHasType("EmptyModel", "B20/Frontend/UiElements/Api/ValueObjects")
        assertHasType("Label", "B20/Frontend/UiElements/Api/Undefined")
        assertHasType("Button", "B20/Frontend/UiElements/Api/Undefined")

        assertHasNotType("Label", "OtherModule/ViewModel/GeneratedElements")
    }

    @Test
    fun `should populate types from hla modules`() {
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
        assertHasClassType("SomeEnum2SwitchGroup", "SomeModule/ViewModel/GeneratedElements") {
            extends = {
                name = "UiElementGroup<SomeEnum2Switch,SomeEnum2>"
            }
        }
        assertHasType("SomeWindow", "SomeModule/ViewModel/GeneratedWindows")
        assertHasType("SomeClassVm", "SomeModule/ViewModel/GeneratedElements")

        assertHasClassType("OptionalSomeClassVm", "SomeModule/ViewModel/GeneratedElements") {
            extends = {
                name = "OptionalUiElement<SomeClassVm,SomeClass>"
            }
        }

        //view types
        assertHasType("OtherClassView", "OtherModule/View/ElementsView")
        assertHasType("SomeEnum2SwitchGroupView", "SomeModule/View/ElementsView")

        assertHasClassType("SomeEnum2SwitchGroupView", "SomeModule/View/ElementsView") {
            extends = {
                name = "UiElementGroupView<SomeEnum2SwitchView>"
            }
        }

        assertHasClassType("OptionalSomeClassView", "SomeModule/View/ElementsView") {
            extends = {
                name = "OptionalUiElementView<SomeClassView>"
            }
        }
    }

    //TODO-REF could be in other file
    @Test
    fun `should map view models to models`() {
        val viewModel = typesWorldApi.getTypeByName(WorldTypeName("SomeEnum2SwitchGroup"))
        val view = vmToViewMapper.map(viewModel)
        assertWorldType(view) {
            name = "SomeEnum2SwitchGroupView"
            path = "SomeModule/View/ElementsView"
        }
    }

    //TODO-REF should be in other file
    @Test
    fun `should calculate correct creation order`() {
        val calculator = CreationOrderCalculator(typesWorldApi)

        val assertCreationOrder = { typeName: String, expectedOrder: Int ->
            val order = calculator.calculateCreationOrder(getTypeByName(typeName))
            assertThat(order)
                .withFailMessage("Type $typeName has wrong creation order, expected $expectedOrder, got $order")
                .isEqualTo(expectedOrder)
        }

        assertCreationOrder("OtherClassView", 1)
        assertCreationOrder("OtherClassGroupView", 2)

        assertCreationOrder("SomeClassView", 1)
        assertCreationOrder("OptionalSomeClassView", 2)
    }

    private fun getTypeByName(name: String): WorldType {
        return typesWorldApi.getTypeByName(WorldTypeName(name))
    }

    private fun assertHasTypeOfName(typeName: String) {
        Assertions.assertThatCode {
            typesWorldApi.getTypeByName(WorldTypeName(typeName))
        }
            .withFailMessage("Type with name $typeName not found")
            .doesNotThrowAnyException()
    }

    private fun assertHasType(typeName: String, typePath: String) {
        assertHasTypeOfName(typeName)

        assertThat(typesWorldApi.hasType(worldType {
            name = typeName
            path = typePath
        }))
            .withFailMessage("Type ${typePath}/${typeName} not found")
            .isTrue()
    }

    private fun assertHasNotType(typeName: String, typePath: String) {
        assertThat(typesWorldApi.hasType(worldType {
            name = typeName
            path = typePath
        }))
            .withFailMessage("Type $typeName found")
            .isFalse()
    }

    private fun assertHasClassType(typeName: String, typePath: String, expectedClass: ExpectedWorldClassType.() -> Unit) {
        assertHasType(typeName, typePath)
        typesWorldApi.getClassType(WorldType(typeName, typePath)).let {
            assertWorldClassType(it, expectedClass)
        }
    }

    private fun assertHasConcreteParametrizedClass(typeName: String, typePath: String, expectedClass: ExpectedWorldConcreteParametrizedClass.() -> Unit) {
        assertHasType(typeName, typePath)
        typesWorldApi.getConcreteParametrizedClass(WorldType(typeName, typePath)).let {
            assertWorldConcreteParametrizedClass(it, expectedClass)
        }
    }
}