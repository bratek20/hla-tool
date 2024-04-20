class OtherId {
    constructor(
        public readonly value: string
    ) {}
}

class OtherClass {
    constructor(
        public readonly id: OtherId,
        public readonly amount: number,
    ) {}
}

