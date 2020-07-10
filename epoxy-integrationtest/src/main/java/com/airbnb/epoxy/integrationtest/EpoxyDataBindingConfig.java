package com.airbnb.epoxy.integrationtest;

import com.airbnb.epoxy.EpoxyDataBindingLayouts;
import com.airbnb.epoxy.EpoxyDataBindingPattern;

@EpoxyDataBindingPattern(rClass = R.class, layoutPrefix = "view_holder")
@EpoxyDataBindingLayouts({R.layout.model_with_data_binding})
interface EpoxyDataBindingConfig {} 
