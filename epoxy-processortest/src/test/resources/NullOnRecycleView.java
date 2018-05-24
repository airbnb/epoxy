package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class NullOnRecycleView extends View {

  public NullOnRecycleView(Context context) {
    super(context);
  }

  @ModelProp(options = Option.NullOnRecycle)
  public void setTitle(@Nullable CharSequence title) {

  }
}
