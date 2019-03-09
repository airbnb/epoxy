package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestCallbackPropView extends View {

  public TestCallbackPropView(Context context) {
    super(context);
  }

  @CallbackProp
  public void setListener(@Nullable View.OnClickListener clickListener) {

  }
}
