// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.ViewModel.UiElements.Api;
using B20.ViewModel.Traits.Api;
using OtherModule.Api;

namespace OtherModule.ViewModel {
    public partial class OtherClassVm: UiElement<OtherClass> {
        public Label Id { get; set; }
        public Label Amount { get; set; }
        protected override void OnUpdate() {
            Id.Update(Model.GetId().Value);
            Amount.Update(Model.GetAmount());
        }
    }

    public class OtherClassVmGroup: UiElementGroup<OtherClassVm, OtherClass> {
        public OtherClassVmGroup(
            B20.Architecture.Contexts.Api.Context c
        ): base(() => c.Get<OtherClassVm>()) {
        }
    }
}