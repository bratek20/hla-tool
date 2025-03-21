// DO NOT EDIT! Autogenerated by HLA tool

using System;
using System.Collections.Generic;
using System.Linq;
using B20.Ext;
using UnityEngine;
using SomeModule.ViewModel;
using B20.View.UiElements.View;
using SomeModule.Api;
using OtherModule.View;
using B20.ViewModel.UiElements.Api;
using OtherModule.Api;

namespace SomeModule.View {
    public class SomeClassView: ElementView<SomeClassVm> {
        [SerializeField]
        LabelView id;
        [SerializeField]
        ButtonView button;
        [SerializeField]
        BoolSwitchView boolSwitch;
        [SerializeField]
        OptionalLabelView optLabel;
        [SerializeField]
        AnimationView someAnimation;
        [SerializeField]
        InputFieldView someInputField;
        [SerializeField]
        ImageView someImage;
        [SerializeField]
        ScrollView someScroll;
        protected override void OnBind() {
            base.OnBind();
            id.Bind(ViewModel.Id);
            button.Bind(ViewModel.Button);
            boolSwitch.Bind(ViewModel.BoolSwitch);
            optLabel.Bind(ViewModel.OptLabel);
            someAnimation.Bind(ViewModel.SomeAnimation);
            someInputField.Bind(ViewModel.SomeInputField);
            someImage.Bind(ViewModel.SomeImage);
            someScroll.Bind(ViewModel.SomeScroll);
        }
    }
}