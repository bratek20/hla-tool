// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using UnityEngine;
using SomeModule.Api;
using SomeModule.ViewModel;
using B20.View.UiElements.View;
using OtherModule.View;
using B20.ViewModel.UiElements.Api;
using OtherModule.Api;

namespace SomeModule.View {
    public class SomeClass2View: ElementView<SomeClass2Vm> {
        [SerializeField]
        BoolSwitchView enabled;
        protected override void OnBind() {
            base.OnBind();
            enabled.Bind(ViewModel.Enabled);
        }
    }
}