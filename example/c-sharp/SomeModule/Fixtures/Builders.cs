// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using SomeModule.Api;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.Fixtures {
    public class SomeModuleBuilders {
        public class SomeClassDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 10;
        }
        public class SomeClass2Def {
            public string Id { get; set; } = "someValue";
            public string[] Names { get; set; } = [];
            public string[] Ids { get; set; } = [];
            public bool Enabled { get; set; } = true;
        }
        public class SomeClass3Def {
            public SomeClass2Def Class2Object { get; set; } = {};
            public string SomeEnum { get; set; } = SomeEnum.VALUE_A.ToString();
            public SomeClass2Def[] Class2List { get; set; } = [];
        }
        public class SomeClass4Def {
            public int OtherId { get; set; } = 0;
            public OtherModule.Builder.OtherClassDef OtherClass { get; set; } = {};
            public int[] OtherIdList { get; set; } = [];
            public OtherModule.Builder.OtherClassDef[] OtherClassList { get; set; } = [];
        }
        public class SomeClass5Def {
            public string Date { get; set; } = "01/01/1970 00:00";
            public TypesModule.Builder.DateRangeDef DateRange { get; set; } = {};
            public DateRangeWrapperDef DateRangeWrapper { get; set; } = {};
            public SomePropertyDef SomeProperty { get; set; } = {};
            public OtherModule.Builder.OtherPropertyDef OtherProperty { get; set; } = {};
        }
        public class SomeClass6Def {
            public SomeClassDef SomeClassOpt { get; set; } = undefined;
            public string OptString { get; set; } = undefined;
            public SomeClass2Def[] Class2List { get; set; } = [];
            public SomeClass6Def[] SameClassList { get; set; } = [];
        }
        public class ClassUsingExternalTypeDef {
            public Optional<legacyType> ExtType { get; set; } = undefined;
        }
        public class ClassHavingOptListDef {
            public SomeClassDef[] OptList { get; set; } = undefined;
        }
        public class ClassHavingOptSimpleVoDef {
            public string OptSimpleVo { get; set; } = undefined;
        }
        public class RecordClassDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 0;
        }
        public class ClassWithOptExamplesDef {
            public int OptInt { get; set; } = 1;
            public int OptIntWrapper { get; set; } = 2;
        }
        public class ClassWithEnumListDef {
            public string[] EnumList { get; set; } = [];
        }
        public class SomeQueryInputDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 0;
        }
        public class SomeHandlerInputDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 0;
        }
        public class SomeHandlerOutputDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 0;
        }
        public class SomePropertyDef {
            public OtherModule.Builder.OtherPropertyDef Other { get; set; } = {};
            public int Id2 { get; set; } = undefined;
            public TypesModule.Builder.DateRangeDef Range { get; set; } = undefined;
            public double DoubleExample { get; set; } = 0;
            public long LongExample { get; set; } = 0;
            public string GoodName { get; set; } = "someValue";
            public object CustomData { get; set; } = {};
        }
        public class SomeProperty2Def {
            public string Value { get; set; } = "someValue";
            public object Custom { get; set; } = {};
            public string SomeEnum { get; set; } = SomeEnum.VALUE_A.ToString();
            public object CustomOpt { get; set; } = undefined;
        }
        public class DateRangeWrapperDef {
            public TypesModule.Builder.DateRangeDef Range { get; set; } = {};
        }
        public class SomeDataDef {
            public OtherModule.Builder.OtherDataDef Other { get; set; } = {};
            public object Custom { get; set; } = {};
            public object CustomOpt { get; set; } = undefined;
            public string GoodDataName { get; set; } = "someValue";
        }
        public class SomeData2Def {
            public string OptEnum { get; set; } = undefined;
            public string OptCustomType { get; set; } = undefined;
        }
        public class SomeEventDef {
            public string SomeField { get; set; } = "someValue";
            public OtherModule.Builder.OtherClassDef OtherClass { get; set; } = {};
        }
        public static SomeId BuildSomeId(string value = "someValue") {
            return new SomeId(value);
        }
        public static SomeIntWrapper BuildSomeIntWrapper(int value = 5) {
            return new SomeIntWrapper(value);
        }
        public static SomeId2 BuildSomeId2(int value = 0) {
            return new SomeId2(value);
        }
        public static SomeClass BuildSomeClass(Action<SomeClassDef> init = () => {}) {
            var def = new SomeClassDef();
            init.Invoke(def);
            return SomeClass.Create(new SomeId(def.id), def.amount);
        }
        public static SomeClass2 BuildSomeClass2(Action<SomeClass2Def> init = () => {}) {
            var def = new SomeClass2Def();
            init.Invoke(def);
            return SomeClass2.Create(new SomeId(def.id), def.names, def.ids.map(it => new SomeId(it)), def.enabled);
        }
        public static SomeClass3 BuildSomeClass3(Action<SomeClass3Def> init = () => {}) {
            var def = new SomeClass3Def();
            init.Invoke(def);
            return SomeClass3.Create(someClass2(def.class2Object), (SomeEnum)Enum.Parse(typeof(SomeEnum), def.someEnum), def.class2List.map(it => someClass2(it)));
        }
        public static SomeClass4 BuildSomeClass4(Action<SomeClass4Def> init = () => {}) {
            var def = new SomeClass4Def();
            init.Invoke(def);
            return SomeClass4.Create(new OtherId(def.otherId), OtherModule.Builder.otherClass(def.otherClass), def.otherIdList.map(it => new OtherId(it)), def.otherClassList.map(it => OtherModule.Builder.otherClass(it)));
        }
        public static SomeClass5 BuildSomeClass5(Action<SomeClass5Def> init = () => {}) {
            var def = new SomeClass5Def();
            init.Invoke(def);
            return SomeClass5.Create(TODO(def.date), TypesModule.Builder.dateRange(def.dateRange), dateRangeWrapper(def.dateRangeWrapper), someProperty(def.someProperty), OtherModule.Builder.otherProperty(def.otherProperty));
        }
        public static SomeClass6 BuildSomeClass6(Action<SomeClass6Def> init = () => {}) {
            var def = new SomeClass6Def();
            init.Invoke(def);
            return SomeClass6.Create(Optional.of(def.someClassOpt).map(it => someClass(it)), Optional.of(def.optString), def.class2List.map(it => someClass2(it)), def.sameClassList.map(it => someClass6(it)));
        }
        public static ClassUsingExternalType BuildClassUsingExternalType(Action<ClassUsingExternalTypeDef> init = () => {}) {
            var def = new ClassUsingExternalTypeDef();
            init.Invoke(def);
            return ClassUsingExternalType.Create(legacyType(def.extType));
        }
        public static ClassHavingOptList BuildClassHavingOptList(Action<ClassHavingOptListDef> init = () => {}) {
            var def = new ClassHavingOptListDef();
            init.Invoke(def);
            return ClassHavingOptList.Create(Optional.of(def.optList).map(it => it.map(it => someClass(it))));
        }
        public static ClassHavingOptSimpleVo BuildClassHavingOptSimpleVo(Action<ClassHavingOptSimpleVoDef> init = () => {}) {
            var def = new ClassHavingOptSimpleVoDef();
            init.Invoke(def);
            return ClassHavingOptSimpleVo.Create(Optional.of(def.optSimpleVo).map(it => new SomeId(it)));
        }
        public static RecordClass BuildRecordClass(Action<RecordClassDef> init = () => {}) {
            var def = new RecordClassDef();
            init.Invoke(def);
            return RecordClass.Create(new SomeId(def.id), def.amount);
        }
        public static ClassWithOptExamples BuildClassWithOptExamples(Action<ClassWithOptExamplesDef> init = () => {}) {
            var def = new ClassWithOptExamplesDef();
            init.Invoke(def);
            return ClassWithOptExamples.Create(Optional.of(def.optInt), Optional.of(def.optIntWrapper).map(it => new SomeIntWrapper(it)));
        }
        public static ClassWithEnumList BuildClassWithEnumList(Action<ClassWithEnumListDef> init = () => {}) {
            var def = new ClassWithEnumListDef();
            init.Invoke(def);
            return ClassWithEnumList.Create(def.enumList.map(it => (SomeEnum2)Enum.Parse(typeof(SomeEnum2), it)));
        }
        public static SomeQueryInput BuildSomeQueryInput(Action<SomeQueryInputDef> init = () => {}) {
            var def = new SomeQueryInputDef();
            init.Invoke(def);
            return SomeQueryInput.Create(new SomeId(def.id), def.amount);
        }
        public static SomeHandlerInput BuildSomeHandlerInput(Action<SomeHandlerInputDef> init = () => {}) {
            var def = new SomeHandlerInputDef();
            init.Invoke(def);
            return SomeHandlerInput.Create(new SomeId(def.id), def.amount);
        }
        public static SomeHandlerOutput BuildSomeHandlerOutput(Action<SomeHandlerOutputDef> init = () => {}) {
            var def = new SomeHandlerOutputDef();
            init.Invoke(def);
            return SomeHandlerOutput.Create(new SomeId(def.id), def.amount);
        }
        public static SomeProperty BuildSomeProperty(Action<SomePropertyDef> init = () => {}) {
            var def = new SomePropertyDef();
            init.Invoke(def);
            return SomeProperty.Create(OtherModule.Builder.otherProperty(def.other), Optional.of(def.id2).map(it => new SomeId2(it)), Optional.of(def.range).map(it => TypesModule.Builder.dateRange(it)), def.doubleExample, def.longExample, def.goodName, def.customData);
        }
        public static SomeProperty2 BuildSomeProperty2(Action<SomeProperty2Def> init = () => {}) {
            var def = new SomeProperty2Def();
            init.Invoke(def);
            return SomeProperty2.Create(def.value, def.custom, (SomeEnum)Enum.Parse(typeof(SomeEnum), def.someEnum), Optional.of(def.customOpt));
        }
        public static DateRangeWrapper BuildDateRangeWrapper(Action<DateRangeWrapperDef> init = () => {}) {
            var def = new DateRangeWrapperDef();
            init.Invoke(def);
            return DateRangeWrapper.Create(TypesModule.Builder.dateRange(def.range));
        }
        public static SomeData BuildSomeData(Action<SomeDataDef> init = () => {}) {
            var def = new SomeDataDef();
            init.Invoke(def);
            return SomeData.Create(OtherModule.Builder.otherData(def.other), def.custom, Optional.of(def.customOpt), def.goodDataName);
        }
        public static SomeData2 BuildSomeData2(Action<SomeData2Def> init = () => {}) {
            var def = new SomeData2Def();
            init.Invoke(def);
            return SomeData2.Create(Optional.of(def.optEnum).map(it => (SomeEnum)Enum.Parse(typeof(SomeEnum), it)), Optional.of(def.optCustomType).map(it => TODO(it)));
        }
        public static SomeEvent BuildSomeEvent(Action<SomeEventDef> init = () => {}) {
            var def = new SomeEventDef();
            init.Invoke(def);
            return SomeEvent.Create(def.someField, OtherModule.Builder.otherClass(def.otherClass));
        }
    }
}