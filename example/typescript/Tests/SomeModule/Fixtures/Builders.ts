// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule.Builder {
    export function someId(value: string = "someValue"): SomeId {
        return new SomeId(value)
    }

    export function someIntWrapper(value: number = 5): SomeIntWrapper {
        return new SomeIntWrapper(value)
    }

    export function someId2(value: number = 0): SomeId2 {
        return new SomeId2(value)
    }

    export interface SomeClassDef {
        id?: string,
        amount?: number,
    }
    export function someClass(def?: SomeClassDef): SomeClass {
        const final_id = def?.id ?? "someValue"
        const final_amount = def?.amount ?? 10

        return SomeClass.create(
            new SomeId(final_id),
            final_amount,
        )
    }

    export interface SomeClass2Def {
        id?: string,
        names?: string[],
        ids?: string[],
        enabled?: boolean,
    }
    export function someClass2(def?: SomeClass2Def): SomeClass2 {
        const final_id = def?.id ?? "someValue"
        const final_names = def?.names ?? []
        const final_ids = def?.ids ?? []
        const final_enabled = def?.enabled ?? true

        return SomeClass2.create(
            new SomeId(final_id),
            final_names,
            final_ids.map(it => new SomeId(it)),
            final_enabled,
        )
    }

    export interface SomeClass3Def {
        class2Object?: SomeClass2Def,
        someEnum?: string,
        class2List?: SomeClass2Def[],
    }
    export function someClass3(def?: SomeClass3Def): SomeClass3 {
        const final_class2Object = def?.class2Object ?? {}
        const final_someEnum = def?.someEnum ?? SomeEnum.VALUE_A.getName()
        const final_class2List = def?.class2List ?? []

        return SomeClass3.create(
            someClass2(final_class2Object),
            SomeEnum.fromName(final_someEnum).get(),
            final_class2List.map(it => someClass2(it)),
        )
    }

    export interface SomeClass4Def {
        otherId?: number,
        otherClass?: OtherModule.Builder.OtherClassDef,
        otherIdList?: number[],
        otherClassList?: OtherModule.Builder.OtherClassDef[],
    }
    export function someClass4(def?: SomeClass4Def): SomeClass4 {
        const final_otherId = def?.otherId ?? 0
        const final_otherClass = def?.otherClass ?? {}
        const final_otherIdList = def?.otherIdList ?? []
        const final_otherClassList = def?.otherClassList ?? []

        return SomeClass4.create(
            new OtherId(final_otherId),
            OtherModule.Builder.otherClass(final_otherClass),
            final_otherIdList.map(it => new OtherId(it)),
            final_otherClassList.map(it => OtherModule.Builder.otherClass(it)),
        )
    }

    export interface SomeClass5Def {
        date?: string,
        dateRange?: TypesModule.Builder.DateRangeDef,
        dateRangeWrapper?: DateRangeWrapperDef,
        someProperty?: SomePropertyDef,
        otherProperty?: OtherModule.Builder.OtherPropertyDef,
    }
    export function someClass5(def?: SomeClass5Def): SomeClass5 {
        const final_date = def?.date ?? "01/01/1970 00:00"
        const final_dateRange = def?.dateRange ?? {}
        const final_dateRangeWrapper = def?.dateRangeWrapper ?? {}
        const final_someProperty = def?.someProperty ?? {}
        const final_otherProperty = def?.otherProperty ?? {}

        return SomeClass5.create(
            TypesModule.CustomTypesMapper.dateCreate(final_date),
            TypesModule.Builder.dateRange(final_dateRange),
            dateRangeWrapper(final_dateRangeWrapper),
            someProperty(final_someProperty),
            OtherModule.Builder.otherProperty(final_otherProperty),
        )
    }

    export interface SomeClass6Def {
        someClassOpt?: SomeClassDef,
        optString?: string,
        class2List?: SomeClass2Def[],
        sameClassList?: SomeClass6Def[],
    }
    export function someClass6(def?: SomeClass6Def): SomeClass6 {
        const final_someClassOpt = def?.someClassOpt ?? undefined
        const final_optString = def?.optString ?? undefined
        const final_class2List = def?.class2List ?? []
        const final_sameClassList = def?.sameClassList ?? []

        return SomeClass6.create(
            Optional.of(final_someClassOpt).map(it => someClass(it)),
            Optional.of(final_optString),
            final_class2List.map(it => someClass2(it)),
            final_sameClassList.map(it => someClass6(it)),
        )
    }

    export interface ClassHavingOptListDef {
        optList?: SomeClassDef[],
    }
    export function classHavingOptList(def?: ClassHavingOptListDef): ClassHavingOptList {
        const final_optList = def?.optList ?? undefined

        return ClassHavingOptList.create(
            Optional.of(final_optList).map(it => it.map(it => someClass(it))),
        )
    }

    export interface ClassHavingOptSimpleVoDef {
        optSimpleVo?: string,
    }
    export function classHavingOptSimpleVo(def?: ClassHavingOptSimpleVoDef): ClassHavingOptSimpleVo {
        const final_optSimpleVo = def?.optSimpleVo ?? undefined

        return ClassHavingOptSimpleVo.create(
            Optional.of(final_optSimpleVo).map(it => new SomeId(it)),
        )
    }

    export interface RecordClassDef {
        id?: string,
        amount?: number,
    }
    export function recordClass(def?: RecordClassDef): RecordClass {
        const final_id = def?.id ?? "someValue"
        const final_amount = def?.amount ?? 0

        return RecordClass.create(
            new SomeId(final_id),
            final_amount,
        )
    }

    export interface ClassWithOptExamplesDef {
        optInt?: number,
        optIntWrapper?: number,
    }
    export function classWithOptExamples(def?: ClassWithOptExamplesDef): ClassWithOptExamples {
        const final_optInt = def?.optInt ?? 1
        const final_optIntWrapper = def?.optIntWrapper ?? 2

        return ClassWithOptExamples.create(
            Optional.of(final_optInt),
            Optional.of(final_optIntWrapper).map(it => new SomeIntWrapper(it)),
        )
    }

    export interface ClassWithEnumListDef {
        enumList?: string[],
    }
    export function classWithEnumList(def?: ClassWithEnumListDef): ClassWithEnumList {
        const final_enumList = def?.enumList ?? []

        return ClassWithEnumList.create(
            final_enumList.map(it => SomeEnum2.fromName(it).get()),
        )
    }

    export interface ClassWithBoolFieldDef {
        boolField?: boolean,
    }
    export function classWithBoolField(def?: ClassWithBoolFieldDef): ClassWithBoolField {
        const final_boolField = def?.boolField ?? false

        return ClassWithBoolField.create(
            final_boolField,
        )
    }

    export interface SomeQueryInputDef {
        id?: string,
        amount?: number,
    }
    export function someQueryInput(def?: SomeQueryInputDef): SomeQueryInput {
        const final_id = def?.id ?? "someValue"
        const final_amount = def?.amount ?? 0

        return SomeQueryInput.create(
            new SomeId(final_id),
            final_amount,
        )
    }

    export interface SomeHandlerInputDef {
        id?: string,
        amount?: number,
    }
    export function someHandlerInput(def?: SomeHandlerInputDef): SomeHandlerInput {
        const final_id = def?.id ?? "someValue"
        const final_amount = def?.amount ?? 0

        return SomeHandlerInput.create(
            new SomeId(final_id),
            final_amount,
        )
    }

    export interface SomeHandlerOutputDef {
        id?: string,
        amount?: number,
    }
    export function someHandlerOutput(def?: SomeHandlerOutputDef): SomeHandlerOutput {
        const final_id = def?.id ?? "someValue"
        const final_amount = def?.amount ?? 0

        return SomeHandlerOutput.create(
            new SomeId(final_id),
            final_amount,
        )
    }

    export interface SomePropertyDef {
        other?: OtherModule.Builder.OtherPropertyDef,
        id2?: number,
        range?: TypesModule.Builder.DateRangeDef,
        doubleExample?: number,
        longExample?: number,
        goodName?: string,
        customData?: any,
    }
    export function someProperty(def?: SomePropertyDef): SomeProperty {
        const final_other = def?.other ?? {}
        const final_id2 = def?.id2 ?? undefined
        const final_range = def?.range ?? undefined
        const final_doubleExample = def?.doubleExample ?? 0
        const final_longExample = def?.longExample ?? 0
        const final_goodName = def?.goodName ?? "someValue"
        const final_customData = def?.customData ?? {}

        return SomeProperty.create(
            OtherModule.Builder.otherProperty(final_other),
            Optional.of(final_id2).map(it => new SomeId2(it)),
            Optional.of(final_range).map(it => TypesModule.Builder.dateRange(it)),
            final_doubleExample,
            final_longExample,
            final_goodName,
            final_customData,
        )
    }

    export interface SomeProperty2Def {
        value?: string,
        custom?: any,
        someEnum?: string,
        customOpt?: any,
    }
    export function someProperty2(def?: SomeProperty2Def): SomeProperty2 {
        const final_value = def?.value ?? "someValue"
        const final_custom = def?.custom ?? {}
        const final_someEnum = def?.someEnum ?? SomeEnum.VALUE_A.getName()
        const final_customOpt = def?.customOpt ?? undefined

        return SomeProperty2.create(
            final_value,
            final_custom,
            SomeEnum.fromName(final_someEnum).get(),
            Optional.of(final_customOpt),
        )
    }

    export interface SomePropertyEntryDef {
        id?: string,
    }
    export function somePropertyEntry(def?: SomePropertyEntryDef): SomePropertyEntry {
        const final_id = def?.id ?? "someValue"

        return SomePropertyEntry.create(
            new SomeId(final_id),
        )
    }

    export interface SomeReferencingPropertyDef {
        referenceId?: string,
    }
    export function someReferencingProperty(def?: SomeReferencingPropertyDef): SomeReferencingProperty {
        const final_referenceId = def?.referenceId ?? "someValue"

        return SomeReferencingProperty.create(
            new SomeId(final_referenceId),
        )
    }

    export interface SomeReferencingPropertyFieldListDef {
        referenceIdList?: string[],
    }
    export function someReferencingPropertyFieldList(def?: SomeReferencingPropertyFieldListDef): SomeReferencingPropertyFieldList {
        const final_referenceIdList = def?.referenceIdList ?? []

        return SomeReferencingPropertyFieldList.create(
            final_referenceIdList.map(it => new SomeId(it)),
        )
    }

    export interface UniqueIdEntryDef {
        id?: string,
    }
    export function uniqueIdEntry(def?: UniqueIdEntryDef): UniqueIdEntry {
        const final_id = def?.id ?? "someValue"

        return UniqueIdEntry.create(
            final_id,
        )
    }

    export interface SomeStructureWithUniqueIdsDef {
        entries?: UniqueIdEntryDef[],
    }
    export function someStructureWithUniqueIds(def?: SomeStructureWithUniqueIdsDef): SomeStructureWithUniqueIds {
        const final_entries = def?.entries ?? []

        return SomeStructureWithUniqueIds.create(
            final_entries.map(it => uniqueIdEntry(it)),
        )
    }

    export interface NestedUniqueIdsDef {
        entries?: UniqueIdEntryDef[],
    }
    export function nestedUniqueIds(def?: NestedUniqueIdsDef): NestedUniqueIds {
        const final_entries = def?.entries ?? []

        return NestedUniqueIds.create(
            final_entries.map(it => uniqueIdEntry(it)),
        )
    }

    export interface SomeStructureWithUniqueNestedIdsDef {
        nestedUniqueIds?: NestedUniqueIdsDef[],
    }
    export function someStructureWithUniqueNestedIds(def?: SomeStructureWithUniqueNestedIdsDef): SomeStructureWithUniqueNestedIds {
        const final_nestedUniqueIds = def?.nestedUniqueIds ?? []

        return SomeStructureWithUniqueNestedIds.create(
            final_nestedUniqueIds.map(it => nestedUniqueIds(it)),
        )
    }

    export interface SomeStructureWithMultipleUniqueNestedIdsDef {
        moreNestedFields?: SomeStructureWithUniqueNestedIdsDef[],
    }
    export function someStructureWithMultipleUniqueNestedIds(def?: SomeStructureWithMultipleUniqueNestedIdsDef): SomeStructureWithMultipleUniqueNestedIds {
        const final_moreNestedFields = def?.moreNestedFields ?? []

        return SomeStructureWithMultipleUniqueNestedIds.create(
            final_moreNestedFields.map(it => someStructureWithUniqueNestedIds(it)),
        )
    }

    export interface NestedValueDef {
        value?: string,
    }
    export function nestedValue(def?: NestedValueDef): NestedValue {
        const final_value = def?.value ?? "someValue"

        return NestedValue.create(
            final_value,
        )
    }

    export interface OptionalFieldPropertyDef {
        optionalField?: NestedValueDef,
    }
    export function optionalFieldProperty(def?: OptionalFieldPropertyDef): OptionalFieldProperty {
        const final_optionalField = def?.optionalField ?? undefined

        return OptionalFieldProperty.create(
            Optional.of(final_optionalField).map(it => nestedValue(it)),
        )
    }

    export interface CustomTypesPropertyDef {
        date?: string,
        dateRange?: TypesModule.Builder.DateRangeDef,
    }
    export function customTypesProperty(def?: CustomTypesPropertyDef): CustomTypesProperty {
        const final_date = def?.date ?? "01/01/1970 00:00"
        const final_dateRange = def?.dateRange ?? {}

        return CustomTypesProperty.create(
            TypesModule.CustomTypesMapper.dateCreate(final_date),
            TypesModule.Builder.dateRange(final_dateRange),
        )
    }

    export interface DateRangeWrapperDef {
        range?: TypesModule.Builder.DateRangeDef,
    }
    export function dateRangeWrapper(def?: DateRangeWrapperDef): DateRangeWrapper {
        const final_range = def?.range ?? {}

        return SomeModule.CustomTypesMapper.dateRangeWrapperCreate(
            TypesModule.Builder.dateRange(final_range),
        )
    }

    export interface SomeDataDef {
        other?: OtherModule.Builder.OtherDataDef,
        custom?: any,
        customOpt?: any,
        goodDataName?: string,
    }
    export function someData(def?: SomeDataDef): SomeData {
        const final_other = def?.other ?? {}
        const final_custom = def?.custom ?? {}
        const final_customOpt = def?.customOpt ?? undefined
        const final_goodDataName = def?.goodDataName ?? "someValue"

        return SomeData.create(
            OtherModule.Builder.otherData(final_other),
            final_custom,
            Optional.of(final_customOpt),
            final_goodDataName,
        )
    }

    export interface SomeData2Def {
        optEnum?: string,
        optCustomType?: string,
    }
    export function someData2(def?: SomeData2Def): SomeData2 {
        const final_optEnum = def?.optEnum ?? undefined
        const final_optCustomType = def?.optCustomType ?? undefined

        return SomeData2.create(
            Optional.of(final_optEnum).map(it => SomeEnum.fromName(it).get()),
            Optional.of(final_optCustomType).map(it => TypesModule.CustomTypesMapper.dateCreate(it)),
        )
    }

    export interface SomeEventDef {
        someField?: string,
        otherClass?: OtherModule.Builder.OtherClassDef,
    }
    export function someEvent(def?: SomeEventDef): SomeEvent {
        const final_someField = def?.someField ?? "someValue"
        const final_otherClass = def?.otherClass ?? {}

        return SomeEvent.create(
            final_someField,
            OtherModule.Builder.otherClass(final_otherClass),
        )
    }
}