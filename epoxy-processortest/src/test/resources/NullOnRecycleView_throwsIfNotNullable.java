package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

import com.airbnb.epoxy.ModelProp.Option;

@ModelView(defaultLayout = 1)
public class NullOnRecycleView_throwsIfNotNullable extends View {

  public NullOnRecycleView_throwsIfNotNullable(Context context) {
    super(context);
  }

  @ModelProp(options = Option.NullOnRecycle)
  public void setTitle(CharSequence title) {

  }
}
