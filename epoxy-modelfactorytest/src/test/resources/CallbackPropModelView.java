package com.airbnb.epoxy;

import android.content.Context;
import android.widget.FrameLayout;
import androidx.annotation.Nullable;

@ModelView(defaultLayout = 1)
public class CallbackPropModelView extends FrameLayout {

  public CallbackPropModelView(Context context) {
    super(context);
  }

  @CallbackProp
  public void setOnClickListener(@Nullable OnClickListener listener) {

  }
}
