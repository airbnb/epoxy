package com.airbnb.epoxy;

import android.content.Context;
import android.support.annotation.Nullable;
import android.widget.FrameLayout;

@ModelView(defaultLayout = 1)
public class CallbackPropModelView extends FrameLayout {

  public CallbackPropModelView(Context context) {
    super(context);
  }

  @CallbackProp
  public void setOnClickListener(@Nullable OnClickListener listener) {

  }
}