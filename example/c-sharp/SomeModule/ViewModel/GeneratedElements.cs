// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.Frontend.Traits;
using B20.Frontend.UiElements;
using SomeModule.Api;
using OtherModule.Api;
using OtherModule.ViewModel;
using TypesModule.Api;
using TypesModule.ViewModel;

namespace SomeModule.ViewModel {
    public partial class SomeClassVm: UiElement<SomeClass> {
        public Label Id { get; set; }
        public Button Button { get; set; }
        protected override List<Type> GetTraitTypes() {
            return new List<Type>() { typeof(Clickable), typeof(Draggable), typeof(WithRect) };
        }
        protected override void OnUpdate() {
            Id.Update(Model.GetId().Value);
        }
    }

    public partial class SomeClass2Vm: UiElement<SomeClass2> {
        public BoolSwitch Enabled { get; set; }
        protected override void OnUpdate() {
            Enabled.Update(Model.GetEnabled());
        }
    }

    public partial class SomeClass3Vm: UiElement<SomeClass3> {
        public SomeClass2Vm Class2Object { get; set; }
        public SomeEnumSwitch SomeEnum { get; set; }
        public SomeClass2VmGroup Class2List { get; set; }
        protected override void OnUpdate() {
            Class2Object.Update(Model.GetClass2Object());
            SomeEnum.Update(Model.GetSomeEnum());
            Class2List.Update(Model.GetClass2List());
        }
    }

    public partial class SomeClass4Vm: UiElement<SomeClass4> {
        public Label OtherId { get; set; }
        public OtherClassVm OtherClass { get; set; }
        protected override void OnUpdate() {
            OtherId.Update(Model.GetOtherId().Value);
            OtherClass.Update(Model.GetOtherClass());
        }
    }

    public partial class SomeClass6Vm: UiElement<SomeClass6> {
        public OptionalSomeClassVm SomeClassOpt { get; set; }
        public SomeClass2VmGroup Class2List { get; set; }
        protected override void OnUpdate() {
            SomeClassOpt.Update(Model.GetSomeClassOpt());
            Class2List.Update(Model.GetClass2List());
        }
    }

    public partial class ClassHavingOptSimpleVoVm: UiElement<ClassHavingOptSimpleVo> {
        public OptionalLabel OptSimpleVo { get; set; }
        protected override void OnUpdate() {
            OptSimpleVo.Update(Model.GetOptSimpleVo().Map( it => it.Value ));
        }
    }

    public partial class ClassWithEnumListVm: UiElement<ClassWithEnumList> {
        public SomeEnum2SwitchGroup EnumList { get; set; }
        protected override void OnUpdate() {
            EnumList.Update(Model.GetEnumList());
        }
    }

    public class SomeEnumSwitch: EnumSwitch<SomeEnum> {
    }

    public class SomeEnum2Switch: EnumSwitch<SomeEnum2> {
    }

    public class SomeClass2VmGroup: UiElementGroup<SomeClass2Vm, SomeClass2> {
        public SomeClass2VmGroup(
            B20.Architecture.Contexts.Api.Context c
        ): base(() => c.Get<SomeClass2Vm>()) {
        }
    }

    public class SomeEnum2SwitchGroup: UiElementGroup<SomeEnum2Switch, SomeEnum2> {
        public SomeEnum2SwitchGroup(
            B20.Architecture.Contexts.Api.Context c
        ): base(() => c.Get<SomeEnum2Switch>()) {
        }
    }

    public class SomeClassVmGroup: UiElementGroup<SomeClassVm, SomeClass> {
        public SomeClassVmGroup(
            B20.Architecture.Contexts.Api.Context c
        ): base(() => c.Get<SomeClassVm>()) {
        }
    }

    public class OptionalSomeClassVm: OptionalUiElement<SomeClassVm, SomeClass> {
        public OptionalSomeClassVm(
            SomeClassVm element
        ): base(element) {
        }
    }
}