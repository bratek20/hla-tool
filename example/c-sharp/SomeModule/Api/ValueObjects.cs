// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using OtherModule.Api;
using TypesModule.Api;
using SimpleModule.Api;

namespace SomeModule.Api {
    public class SomeId: ValueObject {
        public string Value { get; }

        public SomeId(
            string value
        ) {
            Value = value;
        }
        public override string ToString() {
            return Value.ToString();
        }
    }

    public class SomeIntWrapper: ValueObject {
        public int Value { get; }

        public SomeIntWrapper(
            int value
        ) {
            Value = value;
        }
        public override string ToString() {
            return Value.ToString();
        }
    }

    public class SomeStructWithIdSourceNamePartiallyEqualToClassNameId: ValueObject {
        public string Value { get; }

        public SomeStructWithIdSourceNamePartiallyEqualToClassNameId(
            string value
        ) {
            Value = value;
        }
        public override string ToString() {
            return Value.ToString();
        }
    }

    public class SomeId2: ValueObject {
        public int Value { get; }

        public SomeId2(
            int value
        ) {
            Value = value;
        }
        public override string ToString() {
            return Value.ToString();
        }
    }

    public class SomeClass: ValueObject {
        readonly string id;
        readonly int amount;

        public SomeClass(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeClass Create(SomeId id, int amount) {
            return new SomeClass(id.Value, amount);
        }
    }

    public class SomeClass2: ValueObject {
        readonly string id;
        readonly List<string> names;
        readonly List<string> ids;
        readonly bool enabled;

        public SomeClass2(
            string id,
            List<string> names,
            List<string> ids,
            bool enabled
        ) {
            this.id = id;
            this.names = names;
            this.ids = ids;
            this.enabled = enabled;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public List<string> GetNames() {
            return names;
        }
        public List<SomeId> GetIds() {
            return ids.Select(it => new SomeId(it)).ToList();
        }
        public bool GetEnabled() {
            return enabled;
        }
        public static SomeClass2 Create(SomeId id, List<string> names, List<SomeId> ids, bool enabled) {
            return new SomeClass2(id.Value, names, ids.Select(it => it.Value).ToList(), enabled);
        }
    }

    public class SomeClass3: ValueObject {
        readonly SomeClass2 class2Object;
        readonly string someEnum;
        readonly List<SomeClass2> class2List;

        public SomeClass3(
            SomeClass2 class2Object,
            string someEnum,
            List<SomeClass2> class2List
        ) {
            this.class2Object = class2Object;
            this.someEnum = someEnum;
            this.class2List = class2List;
        }
        public SomeClass2 GetClass2Object() {
            return class2Object;
        }
        public SomeEnum GetSomeEnum() {
            return (SomeEnum)Enum.Parse(typeof(SomeEnum), someEnum);
        }
        public List<SomeClass2> GetClass2List() {
            return class2List;
        }
        public static SomeClass3 Create(SomeClass2 class2Object, SomeEnum someEnum, List<SomeClass2> class2List) {
            return new SomeClass3(class2Object, someEnum.ToString(), class2List);
        }
    }

    public class SomeClass4: ValueObject {
        readonly int otherId;
        readonly OtherClass otherClass;
        readonly List<int> otherIdList;
        readonly List<OtherClass> otherClassList;

        public SomeClass4(
            int otherId,
            OtherClass otherClass,
            List<int> otherIdList,
            List<OtherClass> otherClassList
        ) {
            this.otherId = otherId;
            this.otherClass = otherClass;
            this.otherIdList = otherIdList;
            this.otherClassList = otherClassList;
        }
        public OtherId GetOtherId() {
            return new OtherId(otherId);
        }
        public OtherClass GetOtherClass() {
            return otherClass;
        }
        public List<OtherId> GetOtherIdList() {
            return otherIdList.Select(it => new OtherId(it)).ToList();
        }
        public List<OtherClass> GetOtherClassList() {
            return otherClassList;
        }
        public static SomeClass4 Create(OtherId otherId, OtherClass otherClass, List<OtherId> otherIdList, List<OtherClass> otherClassList) {
            return new SomeClass4(otherId.Value, otherClass, otherIdList.Select(it => it.Value).ToList(), otherClassList);
        }
    }

    public class SomeClass5: ValueObject {
        readonly string date;
        readonly SerializedDateRange dateRange;
        readonly SerializedDateRangeWrapper dateRangeWrapper;
        readonly SomeProperty someProperty;
        readonly OtherProperty otherProperty;

        public SomeClass5(
            string date,
            SerializedDateRange dateRange,
            SerializedDateRangeWrapper dateRangeWrapper,
            SomeProperty someProperty,
            OtherProperty otherProperty
        ) {
            this.date = date;
            this.dateRange = dateRange;
            this.dateRangeWrapper = dateRangeWrapper;
            this.someProperty = someProperty;
            this.otherProperty = otherProperty;
        }
        public Date GetDate() {
            return TODO(date);
        }
        public DateRange GetDateRange() {
            return dateRange.ToCustomType();
        }
        public DateRangeWrapper GetDateRangeWrapper() {
            return dateRangeWrapper.ToCustomType();
        }
        public SomeProperty GetSomeProperty() {
            return someProperty;
        }
        public OtherProperty GetOtherProperty() {
            return otherProperty;
        }
        public static SomeClass5 Create(Date date, DateRange dateRange, DateRangeWrapper dateRangeWrapper, SomeProperty someProperty, OtherProperty otherProperty) {
            return new SomeClass5(TODO(date), SerializedDateRange.FromCustomType(dateRange), SerializedDateRangeWrapper.FromCustomType(dateRangeWrapper), someProperty, otherProperty);
        }
    }

    public class SomeClass6: ValueObject {
        readonly SomeClass? someClassOpt;
        readonly string? optString;
        readonly List<SomeClass2> class2List;
        readonly List<SomeClass6> sameClassList;

        public SomeClass6(
            SomeClass? someClassOpt,
            string? optString,
            List<SomeClass2> class2List,
            List<SomeClass6> sameClassList
        ) {
            this.someClassOpt = someClassOpt;
            this.optString = optString;
            this.class2List = class2List;
            this.sameClassList = sameClassList;
        }
        public Optional<SomeClass> GetSomeClassOpt() {
            return Optional<SomeClass>.Of(someClassOpt);
        }
        public Optional<string> GetOptString() {
            return Optional<string>.Of(optString);
        }
        public List<SomeClass2> GetClass2List() {
            return class2List;
        }
        public List<SomeClass6> GetSameClassList() {
            return sameClassList;
        }
        public static SomeClass6 Create(Optional<SomeClass> someClassOpt, Optional<string> optString, List<SomeClass2> class2List, List<SomeClass6> sameClassList) {
            return new SomeClass6(someClassOpt.OrElse(null), optString.OrElse(null), class2List, sameClassList);
        }
    }

    public class ClassHavingOptList: ValueObject {
        readonly List<SomeClass>? optList;

        public ClassHavingOptList(
            List<SomeClass>? optList
        ) {
            this.optList = optList;
        }
        public Optional<List<SomeClass>> GetOptList() {
            return Optional<List<SomeClass>>.Of(optList);
        }
        public static ClassHavingOptList Create(Optional<List<SomeClass>> optList) {
            return new ClassHavingOptList(optList.OrElse(null));
        }
    }

    public class ClassHavingOptSimpleVo: ValueObject {
        readonly string? optSimpleVo;

        public ClassHavingOptSimpleVo(
            string? optSimpleVo
        ) {
            this.optSimpleVo = optSimpleVo;
        }
        public Optional<SomeId> GetOptSimpleVo() {
            return Optional<string>.Of(optSimpleVo).Map(it => new SomeId(it));
        }
        public static ClassHavingOptSimpleVo Create(Optional<SomeId> optSimpleVo) {
            return new ClassHavingOptSimpleVo(optSimpleVo.Map(it => it.Value).OrElse(null));
        }
    }

    public class RecordClass: ValueObject {
        readonly string id;
        readonly int amount;

        public RecordClass(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static RecordClass Create(SomeId id, int amount) {
            return new RecordClass(id.Value, amount);
        }
    }

    public class ClassWithOptExamples: ValueObject {
        readonly int? optInt;
        readonly int? optIntWrapper;

        public ClassWithOptExamples(
            int? optInt,
            int? optIntWrapper
        ) {
            this.optInt = optInt;
            this.optIntWrapper = optIntWrapper;
        }
        public Optional<int> GetOptInt() {
            return Optional<int>.Of(optInt);
        }
        public Optional<SomeIntWrapper> GetOptIntWrapper() {
            return Optional<int>.Of(optIntWrapper).Map(it => new SomeIntWrapper(it));
        }
        public static ClassWithOptExamples Create(Optional<int> optInt, Optional<SomeIntWrapper> optIntWrapper) {
            return new ClassWithOptExamples(optInt.OrElse(null), optIntWrapper.Map(it => it.Value).OrElse(null));
        }
    }

    public class ClassWithEnumList: ValueObject {
        readonly List<string> enumList;

        public ClassWithEnumList(
            List<string> enumList
        ) {
            this.enumList = enumList;
        }
        public List<SomeEnum2> GetEnumList() {
            return enumList.Select(it => (SomeEnum2)Enum.Parse(typeof(SomeEnum2), it)).ToList();
        }
        public static ClassWithEnumList Create(List<SomeEnum2> enumList) {
            return new ClassWithEnumList(enumList.Select(it => it.ToString()).ToList());
        }
    }

    public class ClassWithBoolField: ValueObject {
        readonly bool boolField;

        public ClassWithBoolField(
            bool boolField
        ) {
            this.boolField = boolField;
        }
        public bool GetBoolField() {
            return boolField;
        }
        public static ClassWithBoolField Create(bool boolField) {
            return new ClassWithBoolField(boolField);
        }
    }

    public class SomeQueryInput: ValueObject {
        readonly string id;
        readonly int amount;

        public SomeQueryInput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeQueryInput Create(SomeId id, int amount) {
            return new SomeQueryInput(id.Value, amount);
        }
    }

    public class SomeHandlerInput: ValueObject {
        readonly string id;
        readonly int amount;

        public SomeHandlerInput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeHandlerInput Create(SomeId id, int amount) {
            return new SomeHandlerInput(id.Value, amount);
        }
    }

    public class SomeHandlerOutput: ValueObject {
        readonly string id;
        readonly int amount;

        public SomeHandlerOutput(
            string id,
            int amount
        ) {
            this.id = id;
            this.amount = amount;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public int GetAmount() {
            return amount;
        }
        public static SomeHandlerOutput Create(SomeId id, int amount) {
            return new SomeHandlerOutput(id.Value, amount);
        }
    }

    public class SomeProperty: ValueObject {
        readonly OtherProperty other;
        readonly int? id2;
        readonly SerializedDateRange? range;
        readonly double doubleExample;
        readonly long longExample;
        readonly string goodName;
        readonly B20.Architecture.Structs.Api.Struct customData;

        public SomeProperty(
            OtherProperty other,
            int? id2,
            SerializedDateRange? range,
            double doubleExample,
            long longExample,
            string goodName,
            B20.Architecture.Structs.Api.Struct customData
        ) {
            this.other = other;
            this.id2 = id2;
            this.range = range;
            this.doubleExample = doubleExample;
            this.longExample = longExample;
            this.goodName = goodName;
            this.customData = customData;
        }
        public OtherProperty GetOther() {
            return other;
        }
        public Optional<SomeId2> GetId2() {
            return Optional<int>.Of(id2).Map(it => new SomeId2(it));
        }
        public Optional<DateRange> GetRange() {
            return Optional<SerializedDateRange>.Of(range).Map(it => it.ToCustomType());
        }
        public double GetDoubleExample() {
            return doubleExample;
        }
        public long GetLongExample() {
            return longExample;
        }
        public string GetGoodName() {
            return goodName;
        }
        public B20.Architecture.Structs.Api.Struct GetCustomData() {
            return customData;
        }
        public static SomeProperty Create(OtherProperty other, Optional<SomeId2> id2, Optional<DateRange> range, double doubleExample, long longExample, string goodName, B20.Architecture.Structs.Api.Struct customData) {
            return new SomeProperty(other, id2.Map(it => it.Value).OrElse(null), range.Map(it => SerializedDateRange.FromCustomType(it)).OrElse(null), doubleExample, longExample, goodName, customData);
        }
    }

    public class SomeProperty2: ValueObject {
        readonly string value;
        readonly object custom;
        readonly string someEnum;
        readonly object? customOpt;

        public SomeProperty2(
            string value,
            object custom,
            string someEnum,
            object? customOpt
        ) {
            this.value = value;
            this.custom = custom;
            this.someEnum = someEnum;
            this.customOpt = customOpt;
        }
        public string GetValue() {
            return value;
        }
        public object GetCustom() {
            return custom;
        }
        public SomeEnum GetSomeEnum() {
            return (SomeEnum)Enum.Parse(typeof(SomeEnum), someEnum);
        }
        public Optional<object> GetCustomOpt() {
            return Optional<object>.Of(customOpt);
        }
        public static SomeProperty2 Create(string value, object custom, SomeEnum someEnum, Optional<object> customOpt) {
            return new SomeProperty2(value, custom, someEnum.ToString(), customOpt.OrElse(null));
        }
    }

    public class SomePropertyEntry: ValueObject {
        readonly string id;

        public SomePropertyEntry(
            string id
        ) {
            this.id = id;
        }
        public SomeId GetId() {
            return new SomeId(id);
        }
        public static SomePropertyEntry Create(SomeId id) {
            return new SomePropertyEntry(id.Value);
        }
    }

    public class SomeReferencingProperty: ValueObject {
        readonly string referenceId;

        public SomeReferencingProperty(
            string referenceId
        ) {
            this.referenceId = referenceId;
        }
        public SomeId GetReferenceId() {
            return new SomeId(referenceId);
        }
        public static SomeReferencingProperty Create(SomeId referenceId) {
            return new SomeReferencingProperty(referenceId.Value);
        }
    }

    public class SomeReferencingPropertyFieldList: ValueObject {
        readonly List<string> referenceIdList;

        public SomeReferencingPropertyFieldList(
            List<string> referenceIdList
        ) {
            this.referenceIdList = referenceIdList;
        }
        public List<SomeId> GetReferenceIdList() {
            return referenceIdList.Select(it => new SomeId(it)).ToList();
        }
        public static SomeReferencingPropertyFieldList Create(List<SomeId> referenceIdList) {
            return new SomeReferencingPropertyFieldList(referenceIdList.Select(it => it.Value).ToList());
        }
    }

    public class SomeStructureWithUniqueIds: ValueObject {
        readonly List<UniqueIdEntry> entries;

        public SomeStructureWithUniqueIds(
            List<UniqueIdEntry> entries
        ) {
            this.entries = entries;
        }
        public List<UniqueIdEntry> GetEntries() {
            return entries;
        }
        public static SomeStructureWithUniqueIds Create(List<UniqueIdEntry> entries) {
            return new SomeStructureWithUniqueIds(entries);
        }
    }

    public class NestedUniqueIds: ValueObject {
        readonly List<UniqueIdEntry> entries;

        public NestedUniqueIds(
            List<UniqueIdEntry> entries
        ) {
            this.entries = entries;
        }
        public List<UniqueIdEntry> GetEntries() {
            return entries;
        }
        public static NestedUniqueIds Create(List<UniqueIdEntry> entries) {
            return new NestedUniqueIds(entries);
        }
    }

    public class SomeStructureWithUniqueNestedIds: ValueObject {
        readonly List<NestedUniqueIds> nestedUniqueIds;

        public SomeStructureWithUniqueNestedIds(
            List<NestedUniqueIds> nestedUniqueIds
        ) {
            this.nestedUniqueIds = nestedUniqueIds;
        }
        public List<NestedUniqueIds> GetNestedUniqueIds() {
            return nestedUniqueIds;
        }
        public static SomeStructureWithUniqueNestedIds Create(List<NestedUniqueIds> nestedUniqueIds) {
            return new SomeStructureWithUniqueNestedIds(nestedUniqueIds);
        }
    }

    public class SomeStructureWithMultipleUniqueNestedIds: ValueObject {
        readonly List<SomeStructureWithUniqueNestedIds> moreNestedFields;

        public SomeStructureWithMultipleUniqueNestedIds(
            List<SomeStructureWithUniqueNestedIds> moreNestedFields
        ) {
            this.moreNestedFields = moreNestedFields;
        }
        public List<SomeStructureWithUniqueNestedIds> GetMoreNestedFields() {
            return moreNestedFields;
        }
        public static SomeStructureWithMultipleUniqueNestedIds Create(List<SomeStructureWithUniqueNestedIds> moreNestedFields) {
            return new SomeStructureWithMultipleUniqueNestedIds(moreNestedFields);
        }
    }

    public class NestedValue: ValueObject {
        readonly string value;

        public NestedValue(
            string value
        ) {
            this.value = value;
        }
        public string GetValue() {
            return value;
        }
        public static NestedValue Create(string value) {
            return new NestedValue(value);
        }
    }

    public class OptionalFieldProperty: ValueObject {
        readonly NestedValue? optionalField;

        public OptionalFieldProperty(
            NestedValue? optionalField
        ) {
            this.optionalField = optionalField;
        }
        public Optional<NestedValue> GetOptionalField() {
            return Optional<NestedValue>.Of(optionalField);
        }
        public static OptionalFieldProperty Create(Optional<NestedValue> optionalField) {
            return new OptionalFieldProperty(optionalField.OrElse(null));
        }
    }

    public class CustomTypesProperty: ValueObject {
        readonly string date;
        readonly SerializedDateRange dateRange;

        public CustomTypesProperty(
            string date,
            SerializedDateRange dateRange
        ) {
            this.date = date;
            this.dateRange = dateRange;
        }
        public Date GetDate() {
            return TODO(date);
        }
        public DateRange GetDateRange() {
            return dateRange.ToCustomType();
        }
        public static CustomTypesProperty Create(Date date, DateRange dateRange) {
            return new CustomTypesProperty(TODO(date), SerializedDateRange.FromCustomType(dateRange));
        }
    }

    public class SomeStructWithIdSourceNamePartiallyEqualToClassName: ValueObject {
        readonly string id;

        public SomeStructWithIdSourceNamePartiallyEqualToClassName(
            string id
        ) {
            this.id = id;
        }
        public SomeStructWithIdSourceNamePartiallyEqualToClassNameId GetId() {
            return new SomeStructWithIdSourceNamePartiallyEqualToClassNameId(id);
        }
        public static SomeStructWithIdSourceNamePartiallyEqualToClassName Create(SomeStructWithIdSourceNamePartiallyEqualToClassNameId id) {
            return new SomeStructWithIdSourceNamePartiallyEqualToClassName(id.Value);
        }
    }
}