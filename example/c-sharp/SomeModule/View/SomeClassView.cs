// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using B20.Ext;
using B20.Frontend.Elements.View;
using UnityEngine;
using SomeModule.Api;
using SomeModule.ViewModel;
using OtherModule.Api;
using TypesModule.Api;

namespace SomeModule.View {
    public class SomeClassView: ElementView<SomeClassVm> {
        [SerializeField]
        LabelView id;
        [SerializeField]
        ButtonView button;
        protected override void OnBind() {
            base.OnBind();
            id.Bind(ViewModel.Id);
            button.Bind(ViewModel.Button);
        }
    }
}