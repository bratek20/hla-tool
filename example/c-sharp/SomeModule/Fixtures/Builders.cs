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
            public List<string> Names { get; set; } = new List<string>();
            public List<string> Ids { get; set; } = new List<string>();
            public bool Enabled { get; set; } = true;
        }
        public class SomeClass3Def {
            public Action<SomeClass2Def> Class2Object { get; set; } = (_) => {};
            public string SomeEnum { get; set; } = SomeEnum.VALUE_A.ToString();
            public List<Action<SomeClass2Def>> Class2List { get; set; } = new List<SomeClass2>();
        }
        public class SomeClass4Def {
            public int OtherId { get; set; } = 0;
            public Action<OtherClassDef> OtherClass { get; set; } = (_) => {};
            public List<int> OtherIdList { get; set; } = new List<int>();
            public List<Action<OtherClassDef>> OtherClassList { get; set; } = new List<Action<OtherClassDef>>();
        }
        public class SomeClass5Def {
            public string Date { get; set; } = "01/01/1970 00:00";
            public Action<DateRangeDef> DateRange { get; set; } = (_) => {};
            public Action<DateRangeWrapperDef> DateRangeWrapper { get; set; } = (_) => {};
            public Action<SomePropertyDef> SomeProperty { get; set; } = (_) => {};
            public Action<OtherPropertyDef> OtherProperty { get; set; } = (_) => {};
        }
        public class SomeClass6Def {
            public Action<SomeClassDef>? SomeClassOpt { get; set; } = null;
            public string? OptString { get; set; } = null;
            public List<Action<SomeClass2Def>> Class2List { get; set; } = new List<Action<SomeClass2Def>>();
            public List<Action<SomeClass6Def>> SameClassList { get; set; } = new List<SomeClass6>();
        }
        public class ClassHavingOptListDef {
            public List<Action<SomeClassDef>>? OptList { get; set; } = null;
        }
        public class ClassHavingOptSimpleVoDef {
            public string? OptSimpleVo { get; set; } = null;
        }
        public class RecordClassDef {
            public string Id { get; set; } = "someValue";
            public int Amount { get; set; } = 0;
        }
        public class ClassWithOptExamplesDef {
            public int? OptInt { get; set; } = 1;
            public int? OptIntWrapper { get; set; } = 2;
        }
        public class ClassWithEnumListDef {
            public List<string> EnumList { get; set; } = new List<string>();
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
            public Action<OtherPropertyDef> Other { get; set; } = (_) => {};
            public int? Id2 { get; set; } = null;
            public Action<DateRangeDef>? Range { get; set; } = null;
            public double DoubleExample { get; set; } = 0;
            public long LongExample { get; set; } = 0;
            public string GoodName { get; set; } = "someValue";
            public object CustomData { get; set; } = null;
        }
        public class SomeProperty2Def {
            public string Value { get; set; } = "someValue";
            public object Custom { get; set; } = null;
            public string SomeEnum { get; set; } = SomeEnum.VALUE_A.ToString();
            public object? CustomOpt { get; set; } = null;
        }
        public class DateRangeWrapperDef {
            public Action<DateRangeDef> Range { get; set; } = (_) => {};
        }
        public class SomeDataDef {
            public Action<OtherDataDef> Other { get; set; } = (_) => {};
            public object Custom { get; set; } = null;
            public object? CustomOpt { get; set; } = null;
            public string GoodDataName { get; set; } = "someValue";
        }
        public class SomeData2Def {
            public string? OptEnum { get; set; } = null;
            public string? OptCustomType { get; set; } = null;
        }
        public class SomeEventDef {
            public string SomeField { get; set; } = "someValue";
            public Action<OtherClassDef> OtherClass { get; set; } = (_) => {};
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
        public static SomeClass BuildSomeClass(Action<SomeClassDef> init = null) {
            var def = new SomeClassDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeClass2 BuildSomeClass2(Action<SomeClass2Def> init = null) {
            var def = new SomeClass2Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass2.Create(new SomeId(def.Id), TODO, TODO, def.Enabled);
        }
        public static SomeClass3 BuildSomeClass3(Action<SomeClass3Def> init = null) {
            var def = new SomeClass3Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass3.Create(BuildSomeClass2(def.Class2Object), (SomeEnum)Enum.Parse(typeof(SomeEnum), def.SomeEnum), TODO);
        }
        public static SomeClass4 BuildSomeClass4(Action<SomeClass4Def> init = null) {
            var def = new SomeClass4Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass4.Create(new OtherId(def.OtherId), BuildOtherClass(def.OtherClass), TODO, TODO);
        }
        public static SomeClass5 BuildSomeClass5(Action<SomeClass5Def> init = null) {
            var def = new SomeClass5Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass5.Create(TODO(def.Date), BuildDateRange(def.DateRange), BuildDateRangeWrapper(def.DateRangeWrapper), BuildSomeProperty(def.SomeProperty), BuildOtherProperty(def.OtherProperty));
        }
        public static SomeClass6 BuildSomeClass6(Action<SomeClass6Def> init = null) {
            var def = new SomeClass6Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeClass6.Create(TODO, TODO, TODO, TODO);
        }
        public static ClassHavingOptList BuildClassHavingOptList(Action<ClassHavingOptListDef> init = null) {
            var def = new ClassHavingOptListDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return ClassHavingOptList.Create(TODO);
        }
        public static ClassHavingOptSimpleVo BuildClassHavingOptSimpleVo(Action<ClassHavingOptSimpleVoDef> init = null) {
            var def = new ClassHavingOptSimpleVoDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return ClassHavingOptSimpleVo.Create(TODO);
        }
        public static RecordClass BuildRecordClass(Action<RecordClassDef> init = null) {
            var def = new RecordClassDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return RecordClass.Create(new SomeId(def.Id), def.Amount);
        }
        public static ClassWithOptExamples BuildClassWithOptExamples(Action<ClassWithOptExamplesDef> init = null) {
            var def = new ClassWithOptExamplesDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return ClassWithOptExamples.Create(TODO, TODO);
        }
        public static ClassWithEnumList BuildClassWithEnumList(Action<ClassWithEnumListDef> init = null) {
            var def = new ClassWithEnumListDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return ClassWithEnumList.Create(TODO);
        }
        public static SomeQueryInput BuildSomeQueryInput(Action<SomeQueryInputDef> init = null) {
            var def = new SomeQueryInputDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeQueryInput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeHandlerInput BuildSomeHandlerInput(Action<SomeHandlerInputDef> init = null) {
            var def = new SomeHandlerInputDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeHandlerInput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeHandlerOutput BuildSomeHandlerOutput(Action<SomeHandlerOutputDef> init = null) {
            var def = new SomeHandlerOutputDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeHandlerOutput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeProperty BuildSomeProperty(Action<SomePropertyDef> init = null) {
            var def = new SomePropertyDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeProperty.Create(BuildOtherProperty(def.Other), TODO, TODO, def.DoubleExample, def.LongExample, def.GoodName, def.CustomData);
        }
        public static SomeProperty2 BuildSomeProperty2(Action<SomeProperty2Def> init = null) {
            var def = new SomeProperty2Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeProperty2.Create(def.Value, def.Custom, (SomeEnum)Enum.Parse(typeof(SomeEnum), def.SomeEnum), TODO);
        }
        public static DateRangeWrapper BuildDateRangeWrapper(Action<DateRangeWrapperDef> init = null) {
            var def = new DateRangeWrapperDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return DateRangeWrapper.Create(BuildDateRange(def.Range));
        }
        public static SomeData BuildSomeData(Action<SomeDataDef> init = null) {
            var def = new SomeDataDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeData.Create(BuildOtherData(def.Other), def.Custom, TODO, def.GoodDataName);
        }
        public static SomeData2 BuildSomeData2(Action<SomeData2Def> init = null) {
            var def = new SomeData2Def();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeData2.Create(TODO, TODO);
        }
        public static SomeEvent BuildSomeEvent(Action<SomeEventDef> init = null) {
            var def = new SomeEventDef();
            init.Invoke(def);
            def = def ?? ((_) => {});
            return SomeEvent.Create(def.SomeField, BuildOtherClass(def.OtherClass));
        }
    }
}