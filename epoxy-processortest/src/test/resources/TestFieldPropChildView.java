package com.airbnb.epoxy;

import android.content.Context;

import com.airbnb.epoxy.ModelView;
import com.airbnb.epoxy.TextProp;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)

public class TestFieldPropChildView extends TestFieldPropParentView {
 @TextProp CharSequence textValue;

 public TestFieldPropChildView(Context context) {
   super(context);
 }
}