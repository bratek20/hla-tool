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
}

class SomeProperty {
    private other = new OtherProperty
    private id2? = NUMBER
    private range? = new SerializedDateRange
    private doubleExample = NUMBER
    private longExample = NUMBER
    private gN = STRING

    static create(
        other: OtherProperty,
        id2: Optional<SomeId2>,
        range: Optional<DateRange>,
        doubleExample: number,
        longExample: number,
        goodName: string,
    ): SomeProperty {
        const instance = new SomeProperty()
        instance.other = other
        instance.id2 = id2.map(it => it.value).orElse(undefined)
        instance.range = range.map(it => SerializedDateRange.fromCustomType(it)).orElse(undefined)
        instance.doubleExample = doubleExample
        instance.longExample = longExample
        instance.gN = goodName
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
}

class SomeProperty2 {
    value = STRING
    private custom = ANY
    private someEnum = STRING
    private customOpt? = ANY

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
    private ids = [new SomeId]
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
        instance.ids = ids
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
        return this.ids
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
    private otherIdList = [new OtherId]
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
        instance.otherIdList = otherIdList
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
        return this.otherIdList
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
    private someClassOpt? = new SomeClass
    private optString? = STRING
    private sameClassList = [new SomeClass6]

    static create(
        someClassOpt: Optional<SomeClass>,
        optString: Optional<string>,
        sameClassList: SomeClass6[] = [],
    ): SomeClass6 {
        const instance = new SomeClass6()
        instance.someClassOpt = someClassOpt.orElse(undefined)
        instance.optString = optString.orElse(undefined)
        instance.sameClassList = sameClassList
        return instance
    }

    getSomeClassOpt(): Optional<SomeClass> {
        return Optional.of(this.someClassOpt)
    }

    getOptString(): Optional<string> {
        return Optional.of(this.optString)
    }

    getSameClassList(): SomeClass6[] {
        return this.sameClassList
    }
}