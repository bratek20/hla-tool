// DO NOT EDIT! Autogenerated by HLA tool

class SomeId {
    constructor(
        public readonly value: string
    ) {}

    equals(other: SomeId): boolean {
        return this.value === other.value
    }

    toString(): string {
        return this.value.toString()
    }
}

class SomeIntWrapper {
    constructor(
        public readonly value: number
    ) {}

    equals(other: SomeIntWrapper): boolean {
        return this.value === other.value
    }

    toString(): string {
        return this.value.toString()
    }

    plus(other: SomeIntWrapper): SomeIntWrapper {
        return new SomeIntWrapper(this.value + other.value);
    }

    minus(other: SomeIntWrapper): SomeIntWrapper {
        return new SomeIntWrapper(this.value - other.value);
    }

    times(amount: number): SomeIntWrapper {
        return new SomeIntWrapper(this.value * amount);
    }
}

class SomeId2 {
    constructor(
        public readonly value: number
    ) {}

    equals(other: SomeId2): boolean {
        return this.value === other.value
    }

    toString(): string {
        return this.value.toString()
    }

    plus(other: SomeId2): SomeId2 {
        return new SomeId2(this.value + other.value);
    }

    minus(other: SomeId2): SomeId2 {
        return new SomeId2(this.value - other.value);
    }

    times(amount: number): SomeId2 {
        return new SomeId2(this.value * amount);
    }
}

class SomeClass {
    private id = STRING
    private amount = NUMBER

    static create(
        id: SomeId,
        amount: number,
    ): SomeClass {
        const instance = new SomeClass()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class SomeClass2 {
    private id = STRING
    private names = [STRING]
    private ids = [STRING]
    private enabled = BOOLEAN

    static create(
        id: SomeId,
        names: string[],
        ids: SomeId[],
        enabled: boolean = true,
    ): SomeClass2 {
        const instance = new SomeClass2()
        instance.id = id.value
        instance.names = names
        instance.ids = ids.map(it => it.value)
        instance.enabled = enabled
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getNames(): string[] {
        return this.names
    }

    getIds(): SomeId[] {
        return this.ids.map(it => new SomeId(it))
    }

    getEnabled(): boolean {
        return this.enabled
    }
}

class SomeClass3 {
    private class2Object = new SomeClass2
    private someEnum = STRING
    private class2List = [new SomeClass2]

    static create(
        class2Object: SomeClass2,
        someEnum: SomeEnum,
        class2List: SomeClass2[] = [],
    ): SomeClass3 {
        const instance = new SomeClass3()
        instance.class2Object = class2Object
        instance.someEnum = someEnum.getName()
        instance.class2List = class2List
        return instance
    }

    getClass2Object(): SomeClass2 {
        return this.class2Object
    }

    getSomeEnum(): SomeEnum {
        return SomeEnum.fromName(this.someEnum).get()
    }

    getClass2List(): SomeClass2[] {
        return this.class2List
    }
}

class SomeClass4 {
    private otherId = NUMBER
    private otherClass = new OtherClass
    private otherIdList = [NUMBER]
    private otherClassList = [new OtherClass]

    static create(
        otherId: OtherId,
        otherClass: OtherClass,
        otherIdList: OtherId[],
        otherClassList: OtherClass[],
    ): SomeClass4 {
        const instance = new SomeClass4()
        instance.otherId = otherId.value
        instance.otherClass = otherClass
        instance.otherIdList = otherIdList.map(it => it.value)
        instance.otherClassList = otherClassList
        return instance
    }

    getOtherId(): OtherId {
        return new OtherId(this.otherId)
    }

    getOtherClass(): OtherClass {
        return this.otherClass
    }

    getOtherIdList(): OtherId[] {
        return this.otherIdList.map(it => new OtherId(it))
    }

    getOtherClassList(): OtherClass[] {
        return this.otherClassList
    }
}

class SomeClass5 {
    private date = STRING
    private dateRange = new SerializedDateRange
    private dateRangeWrapper = new SerializedDateRangeWrapper
    private someProperty = new SomeProperty
    private otherProperty = new OtherProperty

    static create(
        date: Date,
        dateRange: DateRange,
        dateRangeWrapper: DateRangeWrapper,
        someProperty: SomeProperty,
        otherProperty: OtherProperty,
    ): SomeClass5 {
        const instance = new SomeClass5()
        instance.date = TypesModule.CustomTypesMapper.dateGetValue(date)
        instance.dateRange = SerializedDateRange.fromCustomType(dateRange)
        instance.dateRangeWrapper = SerializedDateRangeWrapper.fromCustomType(dateRangeWrapper)
        instance.someProperty = someProperty
        instance.otherProperty = otherProperty
        return instance
    }

    getDate(): Date {
        return TypesModule.CustomTypesMapper.dateCreate(this.date)
    }

    getDateRange(): DateRange {
        return this.dateRange.toCustomType()
    }

    getDateRangeWrapper(): DateRangeWrapper {
        return this.dateRangeWrapper.toCustomType()
    }

    getSomeProperty(): SomeProperty {
        return this.someProperty
    }

    getOtherProperty(): OtherProperty {
        return this.otherProperty
    }
}

class SomeClass6 {
    private someClassOpt? = OptionalClass(SomeClass)
    private optString? = OPTIONAL_STRING
    private class2List = [new SomeClass2]
    private sameClassList = [new SomeClass6]

    static create(
        someClassOpt: Optional<SomeClass>,
        optString: Optional<string>,
        class2List: SomeClass2[],
        sameClassList: SomeClass6[] = [],
    ): SomeClass6 {
        const instance = new SomeClass6()
        instance.someClassOpt = someClassOpt.orElse(undefined)
        instance.optString = optString.orElse(undefined)
        instance.class2List = class2List
        instance.sameClassList = sameClassList
        return instance
    }

    getSomeClassOpt(): Optional<SomeClass> {
        return Optional.of(this.someClassOpt)
    }

    getOptString(): Optional<string> {
        return Optional.of(this.optString)
    }

    getClass2List(): SomeClass2[] {
        return this.class2List
    }

    getSameClassList(): SomeClass6[] {
        return this.sameClassList
    }
}

class ClassHavingOptList {
    private optList? = [OptionalClass(SomeClass)]

    static create(
        optList: Optional<SomeClass[]>,
    ): ClassHavingOptList {
        const instance = new ClassHavingOptList()
        instance.optList = optList.orElse(undefined)
        return instance
    }

    getOptList(): Optional<SomeClass[]> {
        return Optional.of(this.optList)
    }
}

class ClassHavingOptSimpleVo {
    private optSimpleVo? = OPTIONAL_STRING

    static create(
        optSimpleVo: Optional<SomeId>,
    ): ClassHavingOptSimpleVo {
        const instance = new ClassHavingOptSimpleVo()
        instance.optSimpleVo = optSimpleVo.map(it => it.value).orElse(undefined)
        return instance
    }

    getOptSimpleVo(): Optional<SomeId> {
        return Optional.of(this.optSimpleVo).map(it => new SomeId(it))
    }
}

class RecordClass {
    private id = STRING
    private amount = NUMBER

    static create(
        id: SomeId,
        amount: number,
    ): RecordClass {
        const instance = new RecordClass()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class ClassWithOptExamples {
    private optInt? = OPTIONAL_NUMBER
    private optIntWrapper? = OPTIONAL_NUMBER

    static create(
        optInt: Optional<number>,
        optIntWrapper: Optional<SomeIntWrapper>,
    ): ClassWithOptExamples {
        const instance = new ClassWithOptExamples()
        instance.optInt = optInt.orElse(undefined)
        instance.optIntWrapper = optIntWrapper.map(it => it.value).orElse(undefined)
        return instance
    }

    getOptInt(): Optional<number> {
        return Optional.of(this.optInt)
    }

    getOptIntWrapper(): Optional<SomeIntWrapper> {
        return Optional.of(this.optIntWrapper).map(it => new SomeIntWrapper(it))
    }
}

class ClassWithEnumList {
    private enumList = [STRING]

    static create(
        enumList: SomeEnum2[],
    ): ClassWithEnumList {
        const instance = new ClassWithEnumList()
        instance.enumList = enumList.map(it => it.getName())
        return instance
    }

    getEnumList(): SomeEnum2[] {
        return this.enumList.map(it => SomeEnum2.fromName(it).get())
    }
}

class ClassWithBoolField {
    private boolField = BOOLEAN

    static create(
        boolField: boolean,
    ): ClassWithBoolField {
        const instance = new ClassWithBoolField()
        instance.boolField = boolField
        return instance
    }

    getBoolField(): boolean {
        return this.boolField
    }
}

class SomeQueryInput {
    private id = STRING
    private amount = NUMBER

    static create(
        id: SomeId,
        amount: number,
    ): SomeQueryInput {
        const instance = new SomeQueryInput()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class SomeHandlerInput {
    private id = STRING
    private amount = NUMBER

    static create(
        id: SomeId,
        amount: number,
    ): SomeHandlerInput {
        const instance = new SomeHandlerInput()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class SomeHandlerOutput {
    private id = STRING
    private amount = NUMBER

    static create(
        id: SomeId,
        amount: number,
    ): SomeHandlerOutput {
        const instance = new SomeHandlerOutput()
        instance.id = id.value
        instance.amount = amount
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }

    getAmount(): number {
        return this.amount
    }
}

class SomeProperty {
    private other = new OtherProperty
    private id2? = OPTIONAL_NUMBER
    private range? = OptionalClass(SerializedDateRange)
    private doubleExample = NUMBER
    private longExample = NUMBER
    private gN = STRING
    private customData = ANY

    static create(
        other: OtherProperty,
        id2: Optional<SomeId2>,
        range: Optional<DateRange>,
        doubleExample: number,
        longExample: number,
        goodName: string,
        customData: any,
    ): SomeProperty {
        const instance = new SomeProperty()
        instance.other = other
        instance.id2 = id2.map(it => it.value).orElse(undefined)
        instance.range = range.map(it => SerializedDateRange.fromCustomType(it)).orElse(undefined)
        instance.doubleExample = doubleExample
        instance.longExample = longExample
        instance.gN = goodName
        instance.customData = customData
        return instance
    }

    getOther(): OtherProperty {
        return this.other
    }

    getId2(): Optional<SomeId2> {
        return Optional.of(this.id2).map(it => new SomeId2(it))
    }

    getRange(): Optional<DateRange> {
        return Optional.of(this.range).map(it => it.toCustomType())
    }

    getDoubleExample(): number {
        return this.doubleExample
    }

    getLongExample(): number {
        return this.longExample
    }

    getGoodName(): string {
        return this.gN
    }

    getCustomData(): any {
        return this.customData
    }
}

class SomeProperty2 {
    value = STRING
    private custom = ANY
    private someEnum = STRING
    private customOpt? = OPTIONAL_ANY

    static create(
        value: string,
        custom: any,
        someEnum: SomeEnum,
        customOpt: Optional<any> = Optional.empty(),
    ): SomeProperty2 {
        const instance = new SomeProperty2()
        instance.value = value
        instance.custom = custom
        instance.someEnum = someEnum.getName()
        instance.customOpt = customOpt.orElse(undefined)
        return instance
    }

    getValue(): string {
        return this.value
    }

    getCustom(): any {
        return this.custom
    }

    getSomeEnum(): SomeEnum {
        return SomeEnum.fromName(this.someEnum).get()
    }

    getCustomOpt(): Optional<any> {
        return Optional.of(this.customOpt)
    }
}

class SomePropertyEntry {
    private id = STRING

    static create(
        id: SomeId,
    ): SomePropertyEntry {
        const instance = new SomePropertyEntry()
        instance.id = id.value
        return instance
    }

    getId(): SomeId {
        return new SomeId(this.id)
    }
}

class SomeReferencingProperty {
    private referenceId = STRING

    static create(
        referenceId: SomeId,
    ): SomeReferencingProperty {
        const instance = new SomeReferencingProperty()
        instance.referenceId = referenceId.value
        return instance
    }

    getReferenceId(): SomeId {
        return new SomeId(this.referenceId)
    }
}

class SomeReferencingPropertyFieldList {
    private referenceIdList = [STRING]

    static create(
        referenceIdList: SomeId[],
    ): SomeReferencingPropertyFieldList {
        const instance = new SomeReferencingPropertyFieldList()
        instance.referenceIdList = referenceIdList.map(it => it.value)
        return instance
    }

    getReferenceIdList(): SomeId[] {
        return this.referenceIdList.map(it => new SomeId(it))
    }
}

class NestedValue {
    private value = STRING

    static create(
        value: string,
    ): NestedValue {
        const instance = new NestedValue()
        instance.value = value
        return instance
    }

    getValue(): string {
        return this.value
    }
}

class OptionalFieldProperty {
    private optionalField? = OptionalClass(NestedValue)

    static create(
        optionalField: Optional<NestedValue>,
    ): OptionalFieldProperty {
        const instance = new OptionalFieldProperty()
        instance.optionalField = optionalField.orElse(undefined)
        return instance
    }

    getOptionalField(): Optional<NestedValue> {
        return Optional.of(this.optionalField)
    }
}

class CustomTypesProperty {
    private date = STRING
    private dateRange = new SerializedDateRange

    static create(
        date: Date,
        dateRange: DateRange,
    ): CustomTypesProperty {
        const instance = new CustomTypesProperty()
        instance.date = TypesModule.CustomTypesMapper.dateGetValue(date)
        instance.dateRange = SerializedDateRange.fromCustomType(dateRange)
        return instance
    }

    getDate(): Date {
        return TypesModule.CustomTypesMapper.dateCreate(this.date)
    }

    getDateRange(): DateRange {
        return this.dateRange.toCustomType()
    }
}