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
        context = Ts.E2E.SetupAndCreateContext({
            dependencyName: DependencyName.SomeModule,
            titleData: builderTD => {
                builderTD.with(SOME_KEY_PROPERTY_KEY, SomeModule.Builder.someProperty(args.someKey ?? {}))
                builderTD.with(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, (args.someSourcePropertyList ?? []).map(it => SomeModule.Builder.somePropertyEntry(it)))
                builderTD.with(SOME_RENAMED_SOURCE_PROPERTY_ENTRY_LIST_PROPERTY_KEY, (args.someRenamedSourcePropertyEntryList ?? []).map(it => SomeModule.Builder.someRenamedSourcePropertyEntry(it)))
                builderTD.with(SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, SomeModule.Builder.someReferencingProperty(args.someReferencingPropertyObject ?? {}))
                builderTD.with(SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, (args.someReferencingPropertyList ?? []).map(it => SomeModule.Builder.someReferencingProperty(it)))
                builderTD.with(SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, (args.someRenamedReferencingPropertyList ?? []).map(it => SomeModule.Builder.someRenamedReferencingProperty(it)))
                builderTD.with(SOME_RENAMED_REFERENCING_RENAMED_PROPERTY_LIST_PROPERTY_KEY, (args.someRenamedReferencingRenamedPropertyList ?? []).map(it => SomeModule.Builder.someRenamedReferencingRenamedProperty(it)))
                builderTD.with(SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY, SomeModule.Builder.someReferencingPropertyFieldList(args.someReferencingPropertyFieldList ?? {}))
                builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST_PROPERTY_KEY, (args.someStructureWithUniqueIdsList ?? []).map(it => SomeModule.Builder.someStructureWithUniqueIds(it)))
                builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS_PROPERTY_KEY, (args.someStructureWithUniqueNestedIds ?? []).map(it => SomeModule.Builder.someStructureWithUniqueNestedIds(it)))
                builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT_PROPERTY_KEY, SomeModule.Builder.someStructureWithUniqueIds(args.someStructureWithUniqueIdsObject ?? {}))
                builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST_PROPERTY_KEY, (args.someStructureWithUniqueIdsMultipleNest ?? []).map(it => SomeModule.Builder.someStructureWithMultipleUniqueNestedIds(it)))
                builderTD.with(SOME_STRUCT_WITH_NESTED_OTHER_CLASS_UNIQUE_IDS_PROPERTY_KEY, (args.someStructWithNestedOtherClassUniqueIds ?? []).map(it => SomeModule.Builder.someStructWithNestedOtherClassUniqueIds(it)))
                builderTD.with(COMPLEX_STRUCTURE_WITH_NESTED_UNIQUE_IDS_PROPERTY_KEY, (args.complexStructureWithNestedUniqueIds ?? []).map(it => SomeModule.Builder.complexStructureWithNestedUniqueIds(it)))
                builderTD.with(REFERENCING_OTHER_PROPERTY_PROPERTY_KEY, OtherModule.Builder.otherProperty(args.referencingOtherProperty ?? {}))
                builderTD.with(OPTIONAL_FIELD_PROPERTIES_PROPERTY_KEY, (args.optionalFieldProperties ?? []).map(it => SomeModule.Builder.optionalFieldProperty(it)))
                builderTD.with(CUSTOM_TYPES_PROPERTY_PROPERTY_KEY, SomeModule.Builder.customTypesProperty(args.customTypesProperty ?? {}))
                builderTD.with(SELF_REFERENCING_PROPERTY_PROPERTY_KEY, (args.selfReferencingProperty ?? []).map(it => SomeModule.Builder.selfReferencingProperty(it)))
                builderTD.with(CUSTOM_TYPES_PROPERTY_OPTIONAL_LIST_PROPERTY_KEY, (args.customTypesPropertyOptionalList ?? []).map(it => SomeModule.Builder.customTypesPropertyOptionalList(it)))
            }
        }).context
    }

    export function test(testName: string, fun: TestFunction) {
        addTest("SomeModule", testName, fun)
    }
}