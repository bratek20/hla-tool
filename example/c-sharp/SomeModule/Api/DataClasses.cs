// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using OtherModule.Api;
using SimpleModule.Api;
using TypesModule.Api;

namespace SomeModule.Api {
    public class SomeData: ValueObject {
        readonly string id;
        readonly OtherData other;
        readonly object custom;
        readonly object? customOpt;
        readonly string goodDataName;

        public SomeData(
            string id,
            OtherData other,
            object custom,
            object? customOpt,
            string goodDataName
        ) {
            this.id = id;
            this.other = other;
            this.custom = custom;
            this.customOpt = customOpt;
            this.goodDataName = goodDataName;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public OtherData GetOther() {
            return other;
        }
        public object GetCustom() {
            return custom;
        }
        public Optional<object> GetCustomOpt() {
            return Optional<object>.Of(customOpt);
        }
        public string GetGoodDataName() {
            return goodDataName;
        }
        public static SomeData Create(SomeId id, OtherData other, object custom, Optional<object> customOpt, string goodDataName) {
            return new SomeData(id.Value, other, custom, customOpt.OrElse(null), goodDataName);
        }
    }

    public class SomeData2: ValueObject {
        readonly string? optEnum;
        readonly string? optCustomType;

        public SomeData2(
            string? optEnum,
            string? optCustomType
        ) {
            this.optEnum = optEnum;
            this.optCustomType = optCustomType;
        }
        public Optional<SomeEnum> GetOptEnum() {
            return Optional<string>.Of(optEnum).Map(it => (SomeEnum)Enum.Parse(typeof(SomeEnum), it));
        }
        public Optional<Date> GetOptCustomType() {
            return Optional<string>.Of(optCustomType).Map(it => TODO(it));
        }
        public static SomeData2 Create(Optional<SomeEnum> optEnum, Optional<Date> optCustomType) {
            return new SomeData2(optEnum.Map(it => it.ToString()).OrElse(null), optCustomType.Map(it => TODO(it)).OrElse(null));
        }
    }
}