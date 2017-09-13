package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

import com.airbnb.epoxy.ModelView.Size;

@ModelView(autoLayout = Size.MATCH_WIDTH_MATCH_HEIGHT)
public class AutoLayoutModelViewMatchParent extends View {

  public AutoLayoutModelViewMatchParent(Context context) {
    super(context);
  }

  @ModelProp
  void setValue(int value) {

  }
}