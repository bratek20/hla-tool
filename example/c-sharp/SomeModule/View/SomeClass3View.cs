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
    public class SomeClass3View: ElementView<SomeClass3Vm> {
        [SerializeField]
        SomeClass2View class2Object;
        [SerializeField]
        EnumSwitchView someEnum;
        [SerializeField]
        SomeClass2GroupView class2List;
        protected override void OnBind() {
            base.OnBind();
            class2Object.Bind(ViewModel.Class2Object);
            someEnum.Bind(ViewModel.SomeEnum);
            class2List.Bind(ViewModel.Class2List);
        }
    }
}