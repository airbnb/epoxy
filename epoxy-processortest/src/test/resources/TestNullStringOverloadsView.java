package com.airbnb.epoxy;

import android.content.Context;
import androidx.annotation.Nullable;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestNullStringOverloadsView extends View {

  public TestNullStringOverloadsView(Context context) {
    super(context);
  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(@Nullable CharSequence title) {

  }
}
