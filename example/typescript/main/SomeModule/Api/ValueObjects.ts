
class SomeId {
    constructor(
        public readonly value: string
    ) {}
}

class SomeClass {
    constructor(
        public readonly id: SomeId,
        public readonly amount: number,
    ) {}
}

class SomeClass2 {
    constructor(
        public readonly id: SomeId,
        public readonly enabled: boolean,
        public readonly names: string[],
        public readonly ids: SomeId[],
    ) {}
}

class SomeClass3 {
    constructor(
        public readonly class2Object: SomeClass2,
        public readonly class2List: SomeClass2[],
        public readonly someEnum: SomeEnum,
    ) {}
}

class SomeClass4 {
    constructor(
        public readonly otherId: OtherId,
        public readonly otherClass: OtherClass,
        public readonly otherIdList: OtherId[],
        public readonly otherClassList: OtherClass[],
    ) {}
}

class SomeClass5 {
    constructor(
        public readonly date: Date,
        public readonly dateRange: DateRange,
        public readonly dateRangeWrapper: DateRangeWrapper,
        public readonly someProperty: SomeProperty,
        public readonly otherProperty: OtherProperty,
    ) {}
}