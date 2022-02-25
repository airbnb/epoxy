package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MANUAL)
public class AutoLayoutModelViewManualLayoutParams extends View {

  public AutoLayoutModelViewManualLayoutParams(Context context) {
    super(context);
    setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
  }

  @ModelProp
  void setValue(int value) {

  }
}