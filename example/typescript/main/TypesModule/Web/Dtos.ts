namespace TypesModule.Web {
    export class DateRangeDto {
        from = STRING
        to = STRING

        toApi(): DateRange {
            return CustomTypesMapper.dateRangeCreate(
                CustomTypesMapper.dateCreate(this.from),
                CustomTypesMapper.dateCreate(this.to),
            )
        }

        static fromApi(api: DateRange): DateRangeDto {
            const dto = new DateRangeDto()
            dto.from = CustomTypesMapper.dateGetValue(CustomTypesMapper.dateRangeGetFrom(api))
            dto.to = CustomTypesMapper.dateGetValue(CustomTypesMapper.dateRangeGetTo(api))
            return dto
        }
    }
}