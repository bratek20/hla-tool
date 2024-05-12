namespace TypesModule {
    export class DateRangeProperty {
        private from = STRING
        private to = STRING

        static create(
            from: Date,
            to: Date,
        ): DateRangeProperty {
            const instance = new DateRangeProperty()
            instance.from = CustomTypesMapper.dateGetValue(from)
            instance.to = CustomTypesMapper.dateGetValue(to)
            return instance
        }

        getFrom(): Date {
            return CustomTypesMapper.dateCreate(this.from)
        }

        getTo(): Date {
            return CustomTypesMapper.dateCreate(this.to)
        }
    }
}