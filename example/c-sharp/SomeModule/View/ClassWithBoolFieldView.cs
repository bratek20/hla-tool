// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using B20.Frontend.Elements.View;
using UnityEngine;
using SomeModule.Api;
using SomeModule.ViewModel;
using OtherModule.Api;
using OtherModule.ViewModel;
using OtherModule.View;
using TypesModule.Api;

namespace SomeModule.View {
    public class ClassWithBoolFieldView: ElementView<ToggleOverride> {
        [SerializeField]
        BoolSwitchView boolField;
        protected override void OnBind() {
            base.OnBind();
            boolField.Bind(ViewModel.BoolField);
        }
    }
}