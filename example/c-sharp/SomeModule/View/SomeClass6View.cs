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
using OtherModule.View;
using TypesModule.Api;
using TypesModule.View;

namespace SomeModule.View {
    public class SomeClass6View: ElementView<SomeClass6Vm> {
        [SerializeField]
        OptionalSomeClassView someClassOpt;
        [SerializeField]
        SomeClass2GroupView class2List;
        protected override void OnBind() {
            base.OnBind();
            someClassOpt.Bind(ViewModel.SomeClassOpt);
            class2List.Bind(ViewModel.Class2List);
        }
    }
}