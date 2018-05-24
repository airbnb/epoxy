package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

import com.airbnb.epoxy.AfterPropsSet;
import com.airbnb.epoxy.CallbackProp;
import com.airbnb.epoxy.ModelView;

@ModelView(autoLayout = ModelView.Size.WRAP_WIDTH_WRAP_HEIGHT)
public class TestFieldPropCallbackPropView extends View {
  @CallbackProp @Nullable OnClickListener value;

  public TestFieldPropCallbackPropView(Context context) {
    super(context);
  }

  @AfterPropsSet
  public void call() { }
}
