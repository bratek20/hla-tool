// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using B20.Ext;
using B20.Frontend.Traits;
using B20.Frontend.UiElements;
using SomeModule.Api;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.ViewModel {
    public partial class SomeClassVm: UiElement<SomeClass> {
        public Label Id { get; set; }
        public Button Button { get; set; }
        protected override List<Type> GetTraitTypes() {
            return new List<Type>() { typeof(Clickable), typeof(Draggable) };
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
        public TODO Class2Object { get; set; }
        public TODO SomeEnum { get; set; }
        public TODO Class2List { get; set; }
        protected override void OnUpdate() {
            Class2Object.Update(Model.GetClass2Object());
            SomeEnum.Update(Model.GetSomeEnum());
            Class2List.Update(Model.GetClass2List());
        }
    }
}