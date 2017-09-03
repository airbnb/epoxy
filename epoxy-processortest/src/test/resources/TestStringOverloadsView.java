package com.airbnb.epoxy;

import android.content.Context;
import android.view.View;

@ModelView(defaultLayout = 1)
public class TestStringOverloadsView extends View {

  public TestStringOverloadsView(Context context) {
    super(context);
  }

  @ModelProp(options = ModelProp.Option.GenerateStringOverloads)
  public void setTitle(CharSequence title) {

  }

  // test setting options via the value param shortcut
  @ModelProp(ModelProp.Option.GenerateStringOverloads)
  public void setTitleViaValueShortcut(CharSequence title) {

  }
}