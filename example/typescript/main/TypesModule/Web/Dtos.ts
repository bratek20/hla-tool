namespace TypesModule.Web {
    export class DateRangeDto {
        from = STRING
        to = STRING

        static toApi(dto: DateRangeDto): DateRange {
            return CustomTypesMapper.dateRangeCreate(
                CustomTypesMapper.dateCreate(dto.from),
                CustomTypesMapper.dateCreate(dto.to),
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