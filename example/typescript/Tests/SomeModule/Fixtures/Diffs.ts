// DO NOT EDIT! Autogenerated by HLA tool

namespace SomeModule {
    export function diffSomeId(given: SomeId, expected: string, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }

    export function diffSomeIntWrapper(given: SomeIntWrapper, expected: number, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }

    export function diffSomeId2(given: SomeId2, expected: number, path: string = ""): string {
        if (given.value != expected) { return `${path}value ${given.value} != ${expected}` }
        return ""
    }

    export function diffSomeEnum(given: SomeEnum, expected: string, path: string = ""): string {
        if (given != SomeEnum.fromName(expected).get()) { return `${path}value ${given.getName()} != ${expected}` }
        return ""
    }

    export function diffSomeEnum2(given: SomeEnum2, expected: string, path: string = ""): string {
        if (given != SomeEnum2.fromName(expected).get()) { return `${path}value ${given.getName()} != ${expected}` }
        return ""
    }

    export interface ExpectedSomeClass {
        id?: string,
        amount?: number,
    }
    export function diffSomeClass(given: SomeClass, expected: ExpectedSomeClass, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.amount !== undefined) {
            if (given.getAmount() != expected.amount) { result.push(`${path}amount ${given.getAmount()} != ${expected.amount}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeClass2 {
        id?: string,
        names?: string[],
        ids?: string[],
        enabled?: boolean,
    }
    export function diffSomeClass2(given: SomeClass2, expected: ExpectedSomeClass2, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.names !== undefined) {
            if (given.getNames().length != expected.names.length) { result.push(`${path}names size ${given.getNames().length} != ${expected.names.length}`) }
            given.getNames().forEach((entry, idx) => { if (entry != expected.names[idx]) { result.push(`${path}names[${idx}] ${entry} != ${expected.names[idx]}`) } })
        }

        if (expected.ids !== undefined) {
            if (given.getIds().length != expected.ids.length) { result.push(`${path}ids size ${given.getIds().length} != ${expected.ids.length}`) }
            given.getIds().forEach((entry, idx) => { if (diffSomeId(entry, expected.ids[idx]) != "") { result.push(diffSomeId(entry, expected.ids[idx], `${path}ids[${idx}].`)) } })
        }

        if (expected.enabled !== undefined) {
            if (given.getEnabled() != expected.enabled) { result.push(`${path}enabled ${given.getEnabled()} != ${expected.enabled}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeClass3 {
        class2Object?: ExpectedSomeClass2,
        someEnum?: string,
        class2List?: ExpectedSomeClass2[],
    }
    export function diffSomeClass3(given: SomeClass3, expected: ExpectedSomeClass3, path: string = ""): string {
        const result: string[] = []

        if (expected.class2Object !== undefined) {
            if (diffSomeClass2(given.getClass2Object(), expected.class2Object) != "") { result.push(diffSomeClass2(given.getClass2Object(), expected.class2Object, `${path}class2Object.`)) }
        }

        if (expected.someEnum !== undefined) {
            if (diffSomeEnum(given.getSomeEnum(), expected.someEnum) != "") { result.push(diffSomeEnum(given.getSomeEnum(), expected.someEnum, `${path}someEnum.`)) }
        }

        if (expected.class2List !== undefined) {
            if (given.getClass2List().length != expected.class2List.length) { result.push(`${path}class2List size ${given.getClass2List().length} != ${expected.class2List.length}`) }
            given.getClass2List().forEach((entry, idx) => { if (diffSomeClass2(entry, expected.class2List[idx]) != "") { result.push(diffSomeClass2(entry, expected.class2List[idx], `${path}class2List[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedSomeClass4 {
        otherId?: number,
        otherClass?: OtherModule.ExpectedOtherClass,
        otherIdList?: number[],
        otherClassList?: OtherModule.ExpectedOtherClass[],
    }
    export function diffSomeClass4(given: SomeClass4, expected: ExpectedSomeClass4, path: string = ""): string {
        const result: string[] = []

        if (expected.otherId !== undefined) {
            if (OtherModule.diffOtherId(given.getOtherId(), expected.otherId) != "") { result.push(OtherModule.diffOtherId(given.getOtherId(), expected.otherId, `${path}otherId.`)) }
        }

        if (expected.otherClass !== undefined) {
            if (OtherModule.diffOtherClass(given.getOtherClass(), expected.otherClass) != "") { result.push(OtherModule.diffOtherClass(given.getOtherClass(), expected.otherClass, `${path}otherClass.`)) }
        }

        if (expected.otherIdList !== undefined) {
            if (given.getOtherIdList().length != expected.otherIdList.length) { result.push(`${path}otherIdList size ${given.getOtherIdList().length} != ${expected.otherIdList.length}`) }
            given.getOtherIdList().forEach((entry, idx) => { if (OtherModule.diffOtherId(entry, expected.otherIdList[idx]) != "") { result.push(OtherModule.diffOtherId(entry, expected.otherIdList[idx], `${path}otherIdList[${idx}].`)) } })
        }

        if (expected.otherClassList !== undefined) {
            if (given.getOtherClassList().length != expected.otherClassList.length) { result.push(`${path}otherClassList size ${given.getOtherClassList().length} != ${expected.otherClassList.length}`) }
            given.getOtherClassList().forEach((entry, idx) => { if (OtherModule.diffOtherClass(entry, expected.otherClassList[idx]) != "") { result.push(OtherModule.diffOtherClass(entry, expected.otherClassList[idx], `${path}otherClassList[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedSomeClass5 {
        date?: string,
        dateRange?: TypesModule.ExpectedDateRange,
        dateRangeWrapper?: ExpectedDateRangeWrapper,
        someProperty?: ExpectedSomeProperty,
        otherProperty?: OtherModule.ExpectedOtherProperty,
    }
    export function diffSomeClass5(given: SomeClass5, expected: ExpectedSomeClass5, path: string = ""): string {
        const result: string[] = []

        if (expected.date !== undefined) {
            if (TypesModule.diffDate(given.getDate(), expected.date) != "") { result.push(TypesModule.diffDate(given.getDate(), expected.date, `${path}date.`)) }
        }

        if (expected.dateRange !== undefined) {
            if (TypesModule.diffDateRange(given.getDateRange(), expected.dateRange) != "") { result.push(TypesModule.diffDateRange(given.getDateRange(), expected.dateRange, `${path}dateRange.`)) }
        }

        if (expected.dateRangeWrapper !== undefined) {
            if (diffDateRangeWrapper(given.getDateRangeWrapper(), expected.dateRangeWrapper) != "") { result.push(diffDateRangeWrapper(given.getDateRangeWrapper(), expected.dateRangeWrapper, `${path}dateRangeWrapper.`)) }
        }

        if (expected.someProperty !== undefined) {
            if (diffSomeProperty(given.getSomeProperty(), expected.someProperty) != "") { result.push(diffSomeProperty(given.getSomeProperty(), expected.someProperty, `${path}someProperty.`)) }
        }

        if (expected.otherProperty !== undefined) {
            if (OtherModule.diffOtherProperty(given.getOtherProperty(), expected.otherProperty) != "") { result.push(OtherModule.diffOtherProperty(given.getOtherProperty(), expected.otherProperty, `${path}otherProperty.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeClass6 {
        someClassOptEmpty?: boolean,
        someClassOpt?: ExpectedSomeClass,
        optStringEmpty?: boolean,
        optString?: string,
        class2List?: ExpectedSomeClass2[],
        sameClassList?: ExpectedSomeClass6[],
    }
    export function diffSomeClass6(given: SomeClass6, expected: ExpectedSomeClass6, path: string = ""): string {
        const result: string[] = []

        if (expected.someClassOptEmpty !== undefined) {
            if (given.getSomeClassOpt().isEmpty() != expected.someClassOptEmpty) { result.push(`${path}someClassOpt empty ${given.getSomeClassOpt().isEmpty()} != ${expected.someClassOptEmpty}`) }
        }

        if (expected.someClassOpt !== undefined) {
            if (diffSomeClass(given.getSomeClassOpt().get(), expected.someClassOpt) != "") { result.push(diffSomeClass(given.getSomeClassOpt().get(), expected.someClassOpt, `${path}someClassOpt.`)) }
        }

        if (expected.optStringEmpty !== undefined) {
            if (given.getOptString().isEmpty() != expected.optStringEmpty) { result.push(`${path}optString empty ${given.getOptString().isEmpty()} != ${expected.optStringEmpty}`) }
        }

        if (expected.optString !== undefined) {
            if (given.getOptString().get() != expected.optString) { result.push(`${path}optString ${given.getOptString().get()} != ${expected.optString}`) }
        }

        if (expected.class2List !== undefined) {
            if (given.getClass2List().length != expected.class2List.length) { result.push(`${path}class2List size ${given.getClass2List().length} != ${expected.class2List.length}`) }
            given.getClass2List().forEach((entry, idx) => { if (diffSomeClass2(entry, expected.class2List[idx]) != "") { result.push(diffSomeClass2(entry, expected.class2List[idx], `${path}class2List[${idx}].`)) } })
        }

        if (expected.sameClassList !== undefined) {
            if (given.getSameClassList().length != expected.sameClassList.length) { result.push(`${path}sameClassList size ${given.getSameClassList().length} != ${expected.sameClassList.length}`) }
            given.getSameClassList().forEach((entry, idx) => { if (diffSomeClass6(entry, expected.sameClassList[idx]) != "") { result.push(diffSomeClass6(entry, expected.sameClassList[idx], `${path}sameClassList[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedClassHavingOptList {
        optListEmpty?: boolean,
        optList?: ExpectedSomeClass[],
    }
    export function diffClassHavingOptList(given: ClassHavingOptList, expected: ExpectedClassHavingOptList, path: string = ""): string {
        const result: string[] = []

        if (expected.optListEmpty !== undefined) {
            if (given.getOptList().isEmpty() != expected.optListEmpty) { result.push(`${path}optList empty ${given.getOptList().isEmpty()} != ${expected.optListEmpty}`) }
        }

        if (expected.optList !== undefined) {
            if (given.getOptList().get().length != expected.optList.length) { result.push(`${path}optList size ${given.getOptList().get().length} != ${expected.optList.length}`) }
            given.getOptList().get().forEach((entry, idx) => { if (diffSomeClass(entry, expected.optList[idx]) != "") { result.push(diffSomeClass(entry, expected.optList[idx], `${path}optList[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedClassHavingOptSimpleVo {
        optSimpleVoEmpty?: boolean,
        optSimpleVo?: string,
    }
    export function diffClassHavingOptSimpleVo(given: ClassHavingOptSimpleVo, expected: ExpectedClassHavingOptSimpleVo, path: string = ""): string {
        const result: string[] = []

        if (expected.optSimpleVoEmpty !== undefined) {
            if (given.getOptSimpleVo().isEmpty() != expected.optSimpleVoEmpty) { result.push(`${path}optSimpleVo empty ${given.getOptSimpleVo().isEmpty()} != ${expected.optSimpleVoEmpty}`) }
        }

        if (expected.optSimpleVo !== undefined) {
            if (diffSomeId(given.getOptSimpleVo().get(), expected.optSimpleVo) != "") { result.push(diffSomeId(given.getOptSimpleVo().get(), expected.optSimpleVo, `${path}optSimpleVo.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedRecordClass {
        id?: string,
        amount?: number,
    }
    export function diffRecordClass(given: RecordClass, expected: ExpectedRecordClass, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.amount !== undefined) {
            if (given.getAmount() != expected.amount) { result.push(`${path}amount ${given.getAmount()} != ${expected.amount}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedClassWithOptExamples {
        optIntEmpty?: boolean,
        optInt?: number,
        optIntWrapperEmpty?: boolean,
        optIntWrapper?: number,
    }
    export function diffClassWithOptExamples(given: ClassWithOptExamples, expected: ExpectedClassWithOptExamples, path: string = ""): string {
        const result: string[] = []

        if (expected.optIntEmpty !== undefined) {
            if (given.getOptInt().isEmpty() != expected.optIntEmpty) { result.push(`${path}optInt empty ${given.getOptInt().isEmpty()} != ${expected.optIntEmpty}`) }
        }

        if (expected.optInt !== undefined) {
            if (given.getOptInt().get() != expected.optInt) { result.push(`${path}optInt ${given.getOptInt().get()} != ${expected.optInt}`) }
        }

        if (expected.optIntWrapperEmpty !== undefined) {
            if (given.getOptIntWrapper().isEmpty() != expected.optIntWrapperEmpty) { result.push(`${path}optIntWrapper empty ${given.getOptIntWrapper().isEmpty()} != ${expected.optIntWrapperEmpty}`) }
        }

        if (expected.optIntWrapper !== undefined) {
            if (diffSomeIntWrapper(given.getOptIntWrapper().get(), expected.optIntWrapper) != "") { result.push(diffSomeIntWrapper(given.getOptIntWrapper().get(), expected.optIntWrapper, `${path}optIntWrapper.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedClassWithEnumList {
        enumList?: string[],
    }
    export function diffClassWithEnumList(given: ClassWithEnumList, expected: ExpectedClassWithEnumList, path: string = ""): string {
        const result: string[] = []

        if (expected.enumList !== undefined) {
            if (given.getEnumList().length != expected.enumList.length) { result.push(`${path}enumList size ${given.getEnumList().length} != ${expected.enumList.length}`) }
            given.getEnumList().forEach((entry, idx) => { if (diffSomeEnum2(entry, expected.enumList[idx]) != "") { result.push(diffSomeEnum2(entry, expected.enumList[idx], `${path}enumList[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedClassWithBoolField {
        boolField?: boolean,
    }
    export function diffClassWithBoolField(given: ClassWithBoolField, expected: ExpectedClassWithBoolField, path: string = ""): string {
        const result: string[] = []

        if (expected.boolField !== undefined) {
            if (given.getBoolField() != expected.boolField) { result.push(`${path}boolField ${given.getBoolField()} != ${expected.boolField}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeQueryInput {
        id?: string,
        amount?: number,
    }
    export function diffSomeQueryInput(given: SomeQueryInput, expected: ExpectedSomeQueryInput, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.amount !== undefined) {
            if (given.getAmount() != expected.amount) { result.push(`${path}amount ${given.getAmount()} != ${expected.amount}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeHandlerInput {
        id?: string,
        amount?: number,
    }
    export function diffSomeHandlerInput(given: SomeHandlerInput, expected: ExpectedSomeHandlerInput, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.amount !== undefined) {
            if (given.getAmount() != expected.amount) { result.push(`${path}amount ${given.getAmount()} != ${expected.amount}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeHandlerOutput {
        id?: string,
        amount?: number,
    }
    export function diffSomeHandlerOutput(given: SomeHandlerOutput, expected: ExpectedSomeHandlerOutput, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        if (expected.amount !== undefined) {
            if (given.getAmount() != expected.amount) { result.push(`${path}amount ${given.getAmount()} != ${expected.amount}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeProperty {
        other?: OtherModule.ExpectedOtherProperty,
        id2Empty?: boolean,
        id2?: number,
        rangeEmpty?: boolean,
        range?: TypesModule.ExpectedDateRange,
        doubleExample?: number,
        longExample?: number,
        goodName?: string,
        customData?: any,
    }
    export function diffSomeProperty(given: SomeProperty, expected: ExpectedSomeProperty, path: string = ""): string {
        const result: string[] = []

        if (expected.other !== undefined) {
            if (OtherModule.diffOtherProperty(given.getOther(), expected.other) != "") { result.push(OtherModule.diffOtherProperty(given.getOther(), expected.other, `${path}other.`)) }
        }

        if (expected.id2Empty !== undefined) {
            if (given.getId2().isEmpty() != expected.id2Empty) { result.push(`${path}id2 empty ${given.getId2().isEmpty()} != ${expected.id2Empty}`) }
        }

        if (expected.id2 !== undefined) {
            if (diffSomeId2(given.getId2().get(), expected.id2) != "") { result.push(diffSomeId2(given.getId2().get(), expected.id2, `${path}id2.`)) }
        }

        if (expected.rangeEmpty !== undefined) {
            if (given.getRange().isEmpty() != expected.rangeEmpty) { result.push(`${path}range empty ${given.getRange().isEmpty()} != ${expected.rangeEmpty}`) }
        }

        if (expected.range !== undefined) {
            if (TypesModule.diffDateRange(given.getRange().get(), expected.range) != "") { result.push(TypesModule.diffDateRange(given.getRange().get(), expected.range, `${path}range.`)) }
        }

        if (expected.doubleExample !== undefined) {
            if (given.getDoubleExample() != expected.doubleExample) { result.push(`${path}doubleExample ${given.getDoubleExample()} != ${expected.doubleExample}`) }
        }

        if (expected.longExample !== undefined) {
            if (given.getLongExample() != expected.longExample) { result.push(`${path}longExample ${given.getLongExample()} != ${expected.longExample}`) }
        }

        if (expected.goodName !== undefined) {
            if (given.getGoodName() != expected.goodName) { result.push(`${path}goodName ${given.getGoodName()} != ${expected.goodName}`) }
        }

        if (expected.customData !== undefined) {
            if (JSON.stringify(given.getCustomData()) != JSON.stringify(expected.customData)) { result.push(`${path}customData ${JSON.stringify(given.getCustomData())} != ${JSON.stringify(expected.customData)}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeProperty2 {
        value?: string,
        custom?: any,
        someEnum?: string,
        customOptEmpty?: boolean,
        customOpt?: any,
    }
    export function diffSomeProperty2(given: SomeProperty2, expected: ExpectedSomeProperty2, path: string = ""): string {
        const result: string[] = []

        if (expected.value !== undefined) {
            if (given.getValue() != expected.value) { result.push(`${path}value ${given.getValue()} != ${expected.value}`) }
        }

        if (expected.custom !== undefined) {
            if (JSON.stringify(given.getCustom()) != JSON.stringify(expected.custom)) { result.push(`${path}custom ${JSON.stringify(given.getCustom())} != ${JSON.stringify(expected.custom)}`) }
        }

        if (expected.someEnum !== undefined) {
            if (diffSomeEnum(given.getSomeEnum(), expected.someEnum) != "") { result.push(diffSomeEnum(given.getSomeEnum(), expected.someEnum, `${path}someEnum.`)) }
        }

        if (expected.customOptEmpty !== undefined) {
            if (given.getCustomOpt().isEmpty() != expected.customOptEmpty) { result.push(`${path}customOpt empty ${given.getCustomOpt().isEmpty()} != ${expected.customOptEmpty}`) }
        }

        if (expected.customOpt !== undefined) {
            if (JSON.stringify(given.getCustomOpt().get()) != JSON.stringify(expected.customOpt)) { result.push(`${path}customOpt ${JSON.stringify(given.getCustomOpt().get())} != ${JSON.stringify(expected.customOpt)}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomePropertyEntry {
        id?: string,
    }
    export function diffSomePropertyEntry(given: SomePropertyEntry, expected: ExpectedSomePropertyEntry, path: string = ""): string {
        const result: string[] = []

        if (expected.id !== undefined) {
            if (diffSomeId(given.getId(), expected.id) != "") { result.push(diffSomeId(given.getId(), expected.id, `${path}id.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeReferencingProperty {
        referenceId?: string,
    }
    export function diffSomeReferencingProperty(given: SomeReferencingProperty, expected: ExpectedSomeReferencingProperty, path: string = ""): string {
        const result: string[] = []

        if (expected.referenceId !== undefined) {
            if (diffSomeId(given.getReferenceId(), expected.referenceId) != "") { result.push(diffSomeId(given.getReferenceId(), expected.referenceId, `${path}referenceId.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeReferencingPropertyFieldList {
        referenceIdList?: string[],
    }
    export function diffSomeReferencingPropertyFieldList(given: SomeReferencingPropertyFieldList, expected: ExpectedSomeReferencingPropertyFieldList, path: string = ""): string {
        const result: string[] = []

        if (expected.referenceIdList !== undefined) {
            if (given.getReferenceIdList().length != expected.referenceIdList.length) { result.push(`${path}referenceIdList size ${given.getReferenceIdList().length} != ${expected.referenceIdList.length}`) }
            given.getReferenceIdList().forEach((entry, idx) => { if (diffSomeId(entry, expected.referenceIdList[idx]) != "") { result.push(diffSomeId(entry, expected.referenceIdList[idx], `${path}referenceIdList[${idx}].`)) } })
        }

        return result.join("\n")
    }

    export interface ExpectedNestedValue {
        value?: string,
    }
    export function diffNestedValue(given: NestedValue, expected: ExpectedNestedValue, path: string = ""): string {
        const result: string[] = []

        if (expected.value !== undefined) {
            if (given.getValue() != expected.value) { result.push(`${path}value ${given.getValue()} != ${expected.value}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedOptionalFieldProperty {
        optionalFieldEmpty?: boolean,
        optionalField?: ExpectedNestedValue,
    }
    export function diffOptionalFieldProperty(given: OptionalFieldProperty, expected: ExpectedOptionalFieldProperty, path: string = ""): string {
        const result: string[] = []

        if (expected.optionalFieldEmpty !== undefined) {
            if (given.getOptionalField().isEmpty() != expected.optionalFieldEmpty) { result.push(`${path}optionalField empty ${given.getOptionalField().isEmpty()} != ${expected.optionalFieldEmpty}`) }
        }

        if (expected.optionalField !== undefined) {
            if (diffNestedValue(given.getOptionalField().get(), expected.optionalField) != "") { result.push(diffNestedValue(given.getOptionalField().get(), expected.optionalField, `${path}optionalField.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedCustomTypesProperty {
        date?: string,
        dateRange?: TypesModule.ExpectedDateRange,
    }
    export function diffCustomTypesProperty(given: CustomTypesProperty, expected: ExpectedCustomTypesProperty, path: string = ""): string {
        const result: string[] = []

        if (expected.date !== undefined) {
            if (TypesModule.diffDate(given.getDate(), expected.date) != "") { result.push(TypesModule.diffDate(given.getDate(), expected.date, `${path}date.`)) }
        }

        if (expected.dateRange !== undefined) {
            if (TypesModule.diffDateRange(given.getDateRange(), expected.dateRange) != "") { result.push(TypesModule.diffDateRange(given.getDateRange(), expected.dateRange, `${path}dateRange.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedDateRangeWrapper {
        range?: TypesModule.ExpectedDateRange,
    }
    export function diffDateRangeWrapper(given: DateRangeWrapper, expected: ExpectedDateRangeWrapper, path: string = ""): string {
        const result: string[] = []

        if (expected.range !== undefined) {
            if (TypesModule.diffDateRange(SomeModule.CustomTypesMapper.dateRangeWrapperGetRange(given), expected.range) != "") { result.push(TypesModule.diffDateRange(SomeModule.CustomTypesMapper.dateRangeWrapperGetRange(given), expected.range, `${path}range.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeData {
        other?: OtherModule.ExpectedOtherData,
        custom?: any,
        customOptEmpty?: boolean,
        customOpt?: any,
        goodDataName?: string,
    }
    export function diffSomeData(given: SomeData, expected: ExpectedSomeData, path: string = ""): string {
        const result: string[] = []

        if (expected.other !== undefined) {
            if (OtherModule.diffOtherData(given.getOther(), expected.other) != "") { result.push(OtherModule.diffOtherData(given.getOther(), expected.other, `${path}other.`)) }
        }

        if (expected.custom !== undefined) {
            if (JSON.stringify(given.getCustom()) != JSON.stringify(expected.custom)) { result.push(`${path}custom ${JSON.stringify(given.getCustom())} != ${JSON.stringify(expected.custom)}`) }
        }

        if (expected.customOptEmpty !== undefined) {
            if (given.getCustomOpt().isEmpty() != expected.customOptEmpty) { result.push(`${path}customOpt empty ${given.getCustomOpt().isEmpty()} != ${expected.customOptEmpty}`) }
        }

        if (expected.customOpt !== undefined) {
            if (JSON.stringify(given.getCustomOpt().get()) != JSON.stringify(expected.customOpt)) { result.push(`${path}customOpt ${JSON.stringify(given.getCustomOpt().get())} != ${JSON.stringify(expected.customOpt)}`) }
        }

        if (expected.goodDataName !== undefined) {
            if (given.getGoodDataName() != expected.goodDataName) { result.push(`${path}goodDataName ${given.getGoodDataName()} != ${expected.goodDataName}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeData2 {
        optEnumEmpty?: boolean,
        optEnum?: string,
        optCustomTypeEmpty?: boolean,
        optCustomType?: string,
    }
    export function diffSomeData2(given: SomeData2, expected: ExpectedSomeData2, path: string = ""): string {
        const result: string[] = []

        if (expected.optEnumEmpty !== undefined) {
            if (given.getOptEnum().isEmpty() != expected.optEnumEmpty) { result.push(`${path}optEnum empty ${given.getOptEnum().isEmpty()} != ${expected.optEnumEmpty}`) }
        }

        if (expected.optEnum !== undefined) {
            if (diffSomeEnum(given.getOptEnum().get(), expected.optEnum) != "") { result.push(diffSomeEnum(given.getOptEnum().get(), expected.optEnum, `${path}optEnum.`)) }
        }

        if (expected.optCustomTypeEmpty !== undefined) {
            if (given.getOptCustomType().isEmpty() != expected.optCustomTypeEmpty) { result.push(`${path}optCustomType empty ${given.getOptCustomType().isEmpty()} != ${expected.optCustomTypeEmpty}`) }
        }

        if (expected.optCustomType !== undefined) {
            if (TypesModule.diffDate(given.getOptCustomType().get(), expected.optCustomType) != "") { result.push(TypesModule.diffDate(given.getOptCustomType().get(), expected.optCustomType, `${path}optCustomType.`)) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeEvent {
        someField?: string,
        otherClass?: OtherModule.ExpectedOtherClass,
    }
    export function diffSomeEvent(given: SomeEvent, expected: ExpectedSomeEvent, path: string = ""): string {
        const result: string[] = []

        if (expected.someField !== undefined) {
            if (given.getSomeField() != expected.someField) { result.push(`${path}someField ${given.getSomeField()} != ${expected.someField}`) }
        }

        if (expected.otherClass !== undefined) {
            if (OtherModule.diffOtherClass(given.getOtherClass(), expected.otherClass) != "") { result.push(OtherModule.diffOtherClass(given.getOtherClass(), expected.otherClass, `${path}otherClass.`)) }
        }

        return result.join("\n")
    }
}