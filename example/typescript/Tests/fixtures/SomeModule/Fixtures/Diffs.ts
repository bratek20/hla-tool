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
        someEnum?: SomeEnum,
        class2List?: ExpectedSomeClass2[],
    }
    export function diffSomeClass3(given: SomeClass3, expected: ExpectedSomeClass3, path: string = ""): string {
        const result: string[] = []

        if (expected.class2Object !== undefined) {
            if (diffSomeClass2(given.getClass2Object(), expected.class2Object) != "") { result.push(diffSomeClass2(given.getClass2Object(), expected.class2Object, `${path}class2Object.`)) }
        }

        if (expected.someEnum !== undefined) {
            if (given.getSomeEnum() != expected.someEnum) { result.push(`${path}someEnum ${given.getSomeEnum()} != ${expected.someEnum}`) }
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
        sameClassList?: ExpectedSomeClass6[],
    }
    export function diffSomeClass6(given: SomeClass6, expected: ExpectedSomeClass6, path: string = ""): string {
        const result: string[] = []

        if (expected.someClassOptEmpty !== undefined) {
            if ((given.getSomeClassOpt() == null) != expected.someClassOptEmpty) { result.push(`${path}someClassOpt empty ${given.getSomeClassOpt() == null} != ${expected.someClassOptEmpty}`) }
        }

        if (expected.someClassOpt !== undefined) {
            if (diffSomeClass(given.getSomeClassOpt().get(), expected.someClassOpt) != "") { result.push(diffSomeClass(given.getSomeClassOpt().get(), expected.someClassOpt, `${path}someClassOpt.`)) }
        }

        if (expected.optStringEmpty !== undefined) {
            if ((given.getOptString() == null) != expected.optStringEmpty) { result.push(`${path}optString empty ${given.getOptString() == null} != ${expected.optStringEmpty}`) }
        }

        if (expected.optString !== undefined) {
            if (given.getOptString().get() != expected.optString) { result.push(`${path}optString ${given.getOptString().get()} != ${expected.optString}`) }
        }

        if (expected.sameClassList !== undefined) {
            if (given.getSameClassList().length != expected.sameClassList.length) { result.push(`${path}sameClassList size ${given.getSameClassList().length} != ${expected.sameClassList.length}`) }
            given.getSameClassList().forEach((entry, idx) => { if (diffSomeClass6(entry, expected.sameClassList[idx]) != "") { result.push(diffSomeClass6(entry, expected.sameClassList[idx], `${path}sameClassList[${idx}].`)) } })
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
            if ((given.getId2() == null) != expected.id2Empty) { result.push(`${path}id2 empty ${given.getId2() == null} != ${expected.id2Empty}`) }
        }

        if (expected.id2 !== undefined) {
            if (diffSomeId2(given.getId2().get(), expected.id2) != "") { result.push(diffSomeId2(given.getId2().get(), expected.id2, `${path}id2.`)) }
        }

        if (expected.rangeEmpty !== undefined) {
            if ((given.getRange() == null) != expected.rangeEmpty) { result.push(`${path}range empty ${given.getRange() == null} != ${expected.rangeEmpty}`) }
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
            if (given.getCustomData() != expected.customData) { result.push(`${path}customData ${given.getCustomData()} != ${expected.customData}`) }
        }

        return result.join("\n")
    }

    export interface ExpectedSomeProperty2 {
        value?: string,
        custom?: any,
        someEnum?: SomeEnum,
        customOptEmpty?: boolean,
        customOpt?: any,
    }
    export function diffSomeProperty2(given: SomeProperty2, expected: ExpectedSomeProperty2, path: string = ""): string {
        const result: string[] = []

        if (expected.value !== undefined) {
            if (given.getValue() != expected.value) { result.push(`${path}value ${given.getValue()} != ${expected.value}`) }
        }

        if (expected.custom !== undefined) {
            if (given.getCustom() != expected.custom) { result.push(`${path}custom ${given.getCustom()} != ${expected.custom}`) }
        }

        if (expected.someEnum !== undefined) {
            if (given.getSomeEnum() != expected.someEnum) { result.push(`${path}someEnum ${given.getSomeEnum()} != ${expected.someEnum}`) }
        }

        if (expected.customOptEmpty !== undefined) {
            if ((given.getCustomOpt() == null) != expected.customOptEmpty) { result.push(`${path}customOpt empty ${given.getCustomOpt() == null} != ${expected.customOptEmpty}`) }
        }

        if (expected.customOpt !== undefined) {
            if (given.getCustomOpt().get() != expected.customOpt) { result.push(`${path}customOpt ${given.getCustomOpt().get()} != ${expected.customOpt}`) }
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
            if (given.getCustom() != expected.custom) { result.push(`${path}custom ${given.getCustom()} != ${expected.custom}`) }
        }

        if (expected.customOptEmpty !== undefined) {
            if ((given.getCustomOpt() == null) != expected.customOptEmpty) { result.push(`${path}customOpt empty ${given.getCustomOpt() == null} != ${expected.customOptEmpty}`) }
        }

        if (expected.customOpt !== undefined) {
            if (given.getCustomOpt().get() != expected.customOpt) { result.push(`${path}customOpt ${given.getCustomOpt().get()} != ${expected.customOpt}`) }
        }

        if (expected.goodDataName !== undefined) {
            if (given.getGoodDataName() != expected.goodDataName) { result.push(`${path}goodDataName ${given.getGoodDataName()} != ${expected.goodDataName}`) }
        }

        return result.join("\n")
    }
}