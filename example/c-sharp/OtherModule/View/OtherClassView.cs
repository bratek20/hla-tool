// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using UnityEngine;
using OtherModule.ViewModel;
using B20.View.UiElements.View;
using OtherModule.Api;

namespace OtherModule.View {
    public class OtherClassView: ElementView<OtherClassVm> {
        [SerializeField]
        LabelView id;
        [SerializeField]
        LabelView amount;
        protected override void OnBind() {
            base.OnBind();
            id.Bind(ViewModel.Id);
            amount.Bind(ViewModel.Amount);
        }
    }
}