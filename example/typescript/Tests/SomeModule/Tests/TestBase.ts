namespace SomeModule {
    export let context: HandlerContext

    export interface SetupArgs {
        someKey?: SomeModule.Builder.SomePropertyDef
        someSourcePropertyList?: SomeModule.Builder.SomePropertyEntryDef[]
        someRenamedSourcePropertyEntryList?: SomeModule.Builder.SomeRenamedSourcePropertyEntryDef[]
        someReferencingPropertyObject?: SomeModule.Builder.SomeReferencingPropertyDef
        someReferencingPropertyList?: SomeModule.Builder.SomeReferencingPropertyDef[]
        someRenamedReferencingPropertyList?: SomeModule.Builder.SomeRenamedReferencingPropertyDef[]
        someRenamedReferencingRenamedPropertyList?: SomeModule.Builder.SomeRenamedReferencingRenamedPropertyDef[]
        someReferencingPropertyFieldList?: SomeModule.Builder.SomeReferencingPropertyFieldListDef
        someStructureWithUniqueIdsList?: SomeModule.Builder.SomeStructureWithUniqueIdsDef[]
        someStructureWithUniqueNestedIds?: SomeModule.Builder.SomeStructureWithUniqueNestedIdsDef[]
        someStructureWithUniqueIdsObject?: SomeModule.Builder.SomeStructureWithUniqueIdsDef
        someStructureWithUniqueIdsMultipleNest?: SomeModule.Builder.SomeStructureWithMultipleUniqueNestedIdsDef[]
        someStructWithNestedOtherClassUniqueIds?: SomeModule.Builder.SomeStructWithNestedOtherClassUniqueIdsDef[]
        complexStructureWithNestedUniqueIds?: SomeModule.Builder.ComplexStructureWithNestedUniqueIdsDef[]
        referencingOtherProperty?: OtherModule.Builder.OtherPropertyDef
        optionalFieldProperties?: SomeModule.Builder.OptionalFieldPropertyDef[]
        customTypesProperty?: SomeModule.Builder.CustomTypesPropertyDef
        selfReferencingProperty?: SomeModule.Builder.SelfReferencingPropertyDef[]
        customTypesPropertyOptionalList?: SomeModule.Builder.CustomTypesPropertyOptionalListDef[]
    }

    export function setup(args: SetupArgs = {}): void {
        context = EmptyContextFor(DependencyName.SomeModule)
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("SomeModule", testName, fun)
    }
}