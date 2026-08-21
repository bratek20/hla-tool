import { COMPLEX_STRUCTURE_WITH_NESTED_UNIQUE_IDS_PROPERTY_KEY, CUSTOM_TYPES_PROPERTY_OPTIONAL_LIST_PROPERTY_KEY, CUSTOM_TYPES_PROPERTY_PROPERTY_KEY, OPTIONAL_FIELD_PROPERTIES_PROPERTY_KEY, REFERENCING_OTHER_PROPERTY_PROPERTY_KEY, SELF_REFERENCING_PROPERTY_PROPERTY_KEY, SOME_KEY2_PROPERTY_KEY, SOME_KEY_PROPERTY_KEY, SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY, SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, SOME_RENAMED_REFERENCING_RENAMED_PROPERTY_LIST_PROPERTY_KEY, SOME_RENAMED_SOURCE_PROPERTY_ENTRY_LIST_PROPERTY_KEY, SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST_PROPERTY_KEY, SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST_PROPERTY_KEY, SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT_PROPERTY_KEY, SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS_PROPERTY_KEY, SOME_STRUCT_WITH_NESTED_OTHER_CLASS_UNIQUE_IDS_PROPERTY_KEY } from "../../../main/SomeModule/Api/PropertyKeys"
import * as OtherModuleBuilder from "../../OtherModule/Fixtures/Builders"
import * as Builder from "../Fixtures/Builders"

export let context: HandlerContext

export interface SetupArgs {
    someKey?: Builder.SomePropertyDef
    someSourcePropertyList?: Builder.SomePropertyEntryDef[]
    someRenamedSourcePropertyEntryList?: Builder.SomeRenamedSourcePropertyEntryDef[]
    someReferencingPropertyObject?: Builder.SomeReferencingPropertyDef
    someReferencingPropertyList?: Builder.SomeReferencingPropertyDef[]
    someRenamedReferencingPropertyList?: Builder.SomeRenamedReferencingPropertyDef[]
    someRenamedReferencingRenamedPropertyList?: Builder.SomeRenamedReferencingRenamedPropertyDef[]
    someReferencingPropertyFieldList?: Builder.SomeReferencingPropertyFieldListDef
    someStructureWithUniqueIdsList?: Builder.SomeStructureWithUniqueIdsDef[]
    someStructureWithUniqueNestedIds?: Builder.SomeStructureWithUniqueNestedIdsDef[]
    someStructureWithUniqueIdsObject?: Builder.SomeStructureWithUniqueIdsDef
    someStructureWithUniqueIdsMultipleNest?: Builder.SomeStructureWithMultipleUniqueNestedIdsDef[]
    someStructWithNestedOtherClassUniqueIds?: Builder.SomeStructWithNestedOtherClassUniqueIdsDef[]
    complexStructureWithNestedUniqueIds?: Builder.ComplexStructureWithNestedUniqueIdsDef[]
    referencingOtherProperty?: OtherModuleBuilder.OtherPropertyDef
    optionalFieldProperties?: Builder.OptionalFieldPropertyDef[]
    customTypesProperty?: Builder.CustomTypesPropertyDef
    selfReferencingProperty?: Builder.SelfReferencingPropertyDef[]
    customTypesPropertyOptionalList?: Builder.CustomTypesPropertyOptionalListDef[]
    someKey2?: Builder.SomePropertyDef
}

export function setup(args: SetupArgs = {}): void {
    context = Ts.E2E.SetupAndCreateContext({
        dependencyName: DependencyName.SomeModule,
        titleData: builderTD => {
            builderTD.with(SOME_KEY_PROPERTY_KEY, Builder.someProperty(args.someKey ?? {}))
            builderTD.with(SOME_SOURCE_PROPERTY_LIST_PROPERTY_KEY, (args.someSourcePropertyList ?? []).map(it => Builder.somePropertyEntry(it)))
            builderTD.with(SOME_RENAMED_SOURCE_PROPERTY_ENTRY_LIST_PROPERTY_KEY, (args.someRenamedSourcePropertyEntryList ?? []).map(it => Builder.someRenamedSourcePropertyEntry(it)))
            builderTD.with(SOME_REFERENCING_PROPERTY_OBJECT_PROPERTY_KEY, Builder.someReferencingProperty(args.someReferencingPropertyObject ?? {}))
            builderTD.with(SOME_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, (args.someReferencingPropertyList ?? []).map(it => Builder.someReferencingProperty(it)))
            builderTD.with(SOME_RENAMED_REFERENCING_PROPERTY_LIST_PROPERTY_KEY, (args.someRenamedReferencingPropertyList ?? []).map(it => Builder.someRenamedReferencingProperty(it)))
            builderTD.with(SOME_RENAMED_REFERENCING_RENAMED_PROPERTY_LIST_PROPERTY_KEY, (args.someRenamedReferencingRenamedPropertyList ?? []).map(it => Builder.someRenamedReferencingRenamedProperty(it)))
            builderTD.with(SOME_REFERENCING_PROPERTY_FIELD_LIST_PROPERTY_KEY, Builder.someReferencingPropertyFieldList(args.someReferencingPropertyFieldList ?? {}))
            builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_LIST_PROPERTY_KEY, (args.someStructureWithUniqueIdsList ?? []).map(it => Builder.someStructureWithUniqueIds(it)))
            builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_NESTED_IDS_PROPERTY_KEY, (args.someStructureWithUniqueNestedIds ?? []).map(it => Builder.someStructureWithUniqueNestedIds(it)))
            builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_OBJECT_PROPERTY_KEY, Builder.someStructureWithUniqueIds(args.someStructureWithUniqueIdsObject ?? {}))
            builderTD.with(SOME_STRUCTURE_WITH_UNIQUE_IDS_MULTIPLE_NEST_PROPERTY_KEY, (args.someStructureWithUniqueIdsMultipleNest ?? []).map(it => Builder.someStructureWithMultipleUniqueNestedIds(it)))
            builderTD.with(SOME_STRUCT_WITH_NESTED_OTHER_CLASS_UNIQUE_IDS_PROPERTY_KEY, (args.someStructWithNestedOtherClassUniqueIds ?? []).map(it => Builder.someStructWithNestedOtherClassUniqueIds(it)))
            builderTD.with(COMPLEX_STRUCTURE_WITH_NESTED_UNIQUE_IDS_PROPERTY_KEY, (args.complexStructureWithNestedUniqueIds ?? []).map(it => Builder.complexStructureWithNestedUniqueIds(it)))
            builderTD.with(REFERENCING_OTHER_PROPERTY_PROPERTY_KEY, OtherModuleBuilder.otherProperty(args.referencingOtherProperty ?? {}))
            builderTD.with(OPTIONAL_FIELD_PROPERTIES_PROPERTY_KEY, (args.optionalFieldProperties ?? []).map(it => Builder.optionalFieldProperty(it)))
            builderTD.with(CUSTOM_TYPES_PROPERTY_PROPERTY_KEY, Builder.customTypesProperty(args.customTypesProperty ?? {}))
            builderTD.with(SELF_REFERENCING_PROPERTY_PROPERTY_KEY, (args.selfReferencingProperty ?? []).map(it => Builder.selfReferencingProperty(it)))
            builderTD.with(CUSTOM_TYPES_PROPERTY_OPTIONAL_LIST_PROPERTY_KEY, (args.customTypesPropertyOptionalList ?? []).map(it => Builder.customTypesPropertyOptionalList(it)))
            builderTD.with(SOME_KEY2_PROPERTY_KEY, Builder.someProperty(args.someKey2 ?? {}))
        }
    }).context
}
