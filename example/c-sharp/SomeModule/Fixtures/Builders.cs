// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using SomeModule.Api;
using OtherModule.Api;
using OtherModule.Fixtures;
using TypesModule.Api;
using TypesModule.Fixtures;

namespace SomeModule.Fixtures {
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
        public string SomeEnum { get; set; } = SomeModule.Api.SomeEnum.VALUE_A.ToString();
        public List<Action<SomeClass2Def>> Class2List { get; set; } = new List<Action<SomeClass2Def>>();
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
        public List<Action<SomeClass6Def>> SameClassList { get; set; } = new List<Action<SomeClass6Def>>();
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

    public class ClassWithBoolFieldDef {
        public bool BoolField { get; set; } = false;
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
        public B20.Architecture.Structs.Api.Struct CustomData { get; set; } = null;
    }

    public class SomeProperty2Def {
        public string Value { get; set; } = "someValue";
        public object Custom { get; set; } = null;
        public string SomeEnum { get; set; } = SomeModule.Api.SomeEnum.VALUE_A.ToString();
        public object? CustomOpt { get; set; } = null;
    }

    public class SomePropertyEntryDef {
        public string Id { get; set; } = "someValue";
    }

    public class SomeReferencingPropertyDef {
        public string ReferenceId { get; set; } = "someValue";
    }

    public class NestedValueDef {
        public string Value { get; set; } = "someValue";
    }

    public class OptionalFieldPropertyDef {
        public Action<NestedValueDef>? OptionalField { get; set; } = null;
    }

    public class CustomTypesPropertyDef {
        public string Date { get; set; } = "01/01/1970 00:00";
        public Action<DateRangeDef> DateRange { get; set; } = (_) => {};
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

    public class SomeModuleBuilders {
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
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeClass2 BuildSomeClass2(Action<SomeClass2Def> init = null) {
            var def = new SomeClass2Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass2.Create(new SomeId(def.Id), def.Names, def.Ids.Select(it => new SomeId(it)).ToList(), def.Enabled);
        }
        public static SomeClass3 BuildSomeClass3(Action<SomeClass3Def> init = null) {
            var def = new SomeClass3Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass3.Create(BuildSomeClass2(def.Class2Object), (SomeEnum)Enum.Parse(typeof(SomeEnum), def.SomeEnum), def.Class2List.Select(it => BuildSomeClass2(it)).ToList());
        }
        public static SomeClass4 BuildSomeClass4(Action<SomeClass4Def> init = null) {
            var def = new SomeClass4Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass4.Create(new OtherId(def.OtherId), OtherModuleBuilders.BuildOtherClass(def.OtherClass), def.OtherIdList.Select(it => new OtherId(it)).ToList(), def.OtherClassList.Select(it => OtherModuleBuilders.BuildOtherClass(it)).ToList());
        }
        public static SomeClass5 BuildSomeClass5(Action<SomeClass5Def> init = null) {
            var def = new SomeClass5Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass5.Create(TODO(def.Date), TypesModuleBuilders.BuildDateRange(def.DateRange), BuildDateRangeWrapper(def.DateRangeWrapper), BuildSomeProperty(def.SomeProperty), OtherModuleBuilders.BuildOtherProperty(def.OtherProperty));
        }
        public static SomeClass6 BuildSomeClass6(Action<SomeClass6Def> init = null) {
            var def = new SomeClass6Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeClass6.Create(Optional<Action<SomeClassDef>>.Of(def.SomeClassOpt).Map(it => BuildSomeClass(it)), Optional<string>.Of(def.OptString), def.Class2List.Select(it => BuildSomeClass2(it)).ToList(), def.SameClassList.Select(it => BuildSomeClass6(it)).ToList());
        }
        public static ClassHavingOptList BuildClassHavingOptList(Action<ClassHavingOptListDef> init = null) {
            var def = new ClassHavingOptListDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return ClassHavingOptList.Create(Optional<List<Action<SomeClassDef>>>.Of(def.OptList).Map(it => it.Select(it => BuildSomeClass(it)).ToList()));
        }
        public static ClassHavingOptSimpleVo BuildClassHavingOptSimpleVo(Action<ClassHavingOptSimpleVoDef> init = null) {
            var def = new ClassHavingOptSimpleVoDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return ClassHavingOptSimpleVo.Create(Optional<string>.Of(def.OptSimpleVo).Map(it => new SomeId(it)));
        }
        public static RecordClass BuildRecordClass(Action<RecordClassDef> init = null) {
            var def = new RecordClassDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return RecordClass.Create(new SomeId(def.Id), def.Amount);
        }
        public static ClassWithOptExamples BuildClassWithOptExamples(Action<ClassWithOptExamplesDef> init = null) {
            var def = new ClassWithOptExamplesDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return ClassWithOptExamples.Create(Optional<int>.Of(def.OptInt), Optional<int>.Of(def.OptIntWrapper).Map(it => new SomeIntWrapper(it)));
        }
        public static ClassWithEnumList BuildClassWithEnumList(Action<ClassWithEnumListDef> init = null) {
            var def = new ClassWithEnumListDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return ClassWithEnumList.Create(def.EnumList.Select(it => (SomeEnum2)Enum.Parse(typeof(SomeEnum2), it)).ToList());
        }
        public static ClassWithBoolField BuildClassWithBoolField(Action<ClassWithBoolFieldDef> init = null) {
            var def = new ClassWithBoolFieldDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return ClassWithBoolField.Create(def.BoolField);
        }
        public static SomeQueryInput BuildSomeQueryInput(Action<SomeQueryInputDef> init = null) {
            var def = new SomeQueryInputDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeQueryInput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeHandlerInput BuildSomeHandlerInput(Action<SomeHandlerInputDef> init = null) {
            var def = new SomeHandlerInputDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeHandlerInput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeHandlerOutput BuildSomeHandlerOutput(Action<SomeHandlerOutputDef> init = null) {
            var def = new SomeHandlerOutputDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeHandlerOutput.Create(new SomeId(def.Id), def.Amount);
        }
        public static SomeProperty BuildSomeProperty(Action<SomePropertyDef> init = null) {
            var def = new SomePropertyDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeProperty.Create(OtherModuleBuilders.BuildOtherProperty(def.Other), Optional<int>.Of(def.Id2).Map(it => new SomeId2(it)), Optional<Action<DateRangeDef>>.Of(def.Range).Map(it => TypesModuleBuilders.BuildDateRange(it)), def.DoubleExample, def.LongExample, def.GoodName, def.CustomData);
        }
        public static SomeProperty2 BuildSomeProperty2(Action<SomeProperty2Def> init = null) {
            var def = new SomeProperty2Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeProperty2.Create(def.Value, def.Custom, (SomeEnum)Enum.Parse(typeof(SomeEnum), def.SomeEnum), Optional<object>.Of(def.CustomOpt));
        }
        public static SomePropertyEntry BuildSomePropertyEntry(Action<SomePropertyEntryDef> init = null) {
            var def = new SomePropertyEntryDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomePropertyEntry.Create(new SomeId(def.Id));
        }
        public static SomeReferencingProperty BuildSomeReferencingProperty(Action<SomeReferencingPropertyDef> init = null) {
            var def = new SomeReferencingPropertyDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeReferencingProperty.Create(new SomeId(def.ReferenceId));
        }
        public static NestedValue BuildNestedValue(Action<NestedValueDef> init = null) {
            var def = new NestedValueDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return NestedValue.Create(def.Value);
        }
        public static OptionalFieldProperty BuildOptionalFieldProperty(Action<OptionalFieldPropertyDef> init = null) {
            var def = new OptionalFieldPropertyDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return OptionalFieldProperty.Create(Optional<Action<NestedValueDef>>.Of(def.OptionalField).Map(it => BuildNestedValue(it)));
        }
        public static CustomTypesProperty BuildCustomTypesProperty(Action<CustomTypesPropertyDef> init = null) {
            var def = new CustomTypesPropertyDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return CustomTypesProperty.Create(TODO(def.Date), TypesModuleBuilders.BuildDateRange(def.DateRange));
        }
        public static DateRangeWrapper BuildDateRangeWrapper(Action<DateRangeWrapperDef> init = null) {
            var def = new DateRangeWrapperDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return DateRangeWrapper.Create(TypesModuleBuilders.BuildDateRange(def.Range));
        }
        public static SomeData BuildSomeData(Action<SomeDataDef> init = null) {
            var def = new SomeDataDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeData.Create(OtherModuleBuilders.BuildOtherData(def.Other), def.Custom, Optional<object>.Of(def.CustomOpt), def.GoodDataName);
        }
        public static SomeData2 BuildSomeData2(Action<SomeData2Def> init = null) {
            var def = new SomeData2Def();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeData2.Create(Optional<string>.Of(def.OptEnum).Map(it => (SomeEnum)Enum.Parse(typeof(SomeEnum), it)), Optional<string>.Of(def.OptCustomType).Map(it => TODO(it)));
        }
        public static SomeEvent BuildSomeEvent(Action<SomeEventDef> init = null) {
            var def = new SomeEventDef();
            init = init ?? ((_) => {});
            init.Invoke(def);
            return SomeEvent.Create(def.SomeField, OtherModuleBuilders.BuildOtherClass(def.OtherClass));
        }
    }
}