// DO NOT EDIT! Autogenerated by HLA tool

class SomeData {
    private other = new OtherData
    private custom = ANY
    private customOpt? = ANY
    private gDN = STRING

    static create(
        other: OtherData,
        custom: any,
        customOpt: Optional<any>,
        goodDataName: string,
    ): SomeData {
        const instance = new SomeData()
        instance.other = other
        instance.custom = custom
        instance.customOpt = customOpt.orElse(undefined)
        instance.gDN = goodDataName
        return instance
    }

    getOther(): OtherData {
        return this.other
    }

    getCustom(): any {
        return this.custom
    }

    getCustomOpt(): Optional<any> {
        return Optional.of(this.customOpt)
    }

    getGoodDataName(): string {
        return this.gDN
    }

    setOther(other: OtherData): void {
        this.other = other
    }

    setCustom(custom: any): void {
        this.custom = custom
    }

    setCustomOpt(customOpt: Optional<any>): void {
        this.customOpt = customOpt.orElse(undefined)
    }

    setGoodDataName(goodDataName: string): void {
        this.gDN = goodDataName
    }
}

class SomeData2 {
    private optEnum? = STRING
    private optCustomType? = STRING

    static create(
        optEnum: Optional<SomeEnum>,
        optCustomType: Optional<Date>,
    ): SomeData2 {
        const instance = new SomeData2()
        instance.optEnum = optEnum.orElse(undefined).map(it => it.getName())
        instance.optCustomType = optCustomType.orElse(undefined).map(it => TypesModule.CustomTypesMapper.dateGetValue(it))
        return instance
    }

    getOptEnum(): Optional<SomeEnum> {
        return Optional.of(this.optEnum).map(it => SomeEnum.fromName(it).get())
    }

    getOptCustomType(): Optional<Date> {
        return Optional.of(this.optCustomType).map(it => TypesModule.CustomTypesMapper.dateCreate(it))
    }

    setOptEnum(optEnum: Optional<SomeEnum>): void {
        this.optEnum = optEnum.orElse(undefined).map(it => it.getName())
    }

    setOptCustomType(optCustomType: Optional<Date>): void {
        this.optCustomType = optCustomType.orElse(undefined).map(it => TypesModule.CustomTypesMapper.dateGetValue(it))
    }
}